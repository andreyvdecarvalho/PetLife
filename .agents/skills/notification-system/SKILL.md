---
name: notification-system
description: >
  Skill de implementação do sistema de notificações push e in-app do PetLife.
  Ativa quando o agente precisa implementar jobs de agendamento com Spring Scheduler,
  integração com FCM/APNs, processamento de filas (RabbitMQ/SQS), regras de horário
  de não perturbe, ou gerenciamento da central de notificações.
  Palavras-chave: notificação, push, FCM, Firebase, APNs, Apple Push, RabbitMQ, SQS,
  scheduler, cron, job, fila, mensageria, Spring Scheduler, @Scheduled.
---

# Skill: Sistema de Notificações — PetLife (Spring Boot)

## Visão Geral

O sistema de notificações é **transversal** — consome dados de todos os módulos para gerar lembretes automáticos. É composto por:

1. **NotificationScheduler** — Jobs `@Scheduled` do Spring que verificam eventos pendentes
2. **NotificationProducer** — Publica mensagens na fila RabbitMQ/SQS
3. **NotificationConsumer** — Consome a fila e despacha via FCM/APNs
4. **NotificationRepository** — Persiste notificações in-app no PostgreSQL

---

## Tipos de Notificação e Regras

| Tipo                   | Gatilho                       | Antecedência     | Conta no limite diário? |
|------------------------|-------------------------------|------------------|-------------------------|
| `VACCINATION_DUE`      | `next_dose_date`              | 7 dias + dia D   | ✅ Sim                  |
| `MEDICATION_DOSE`      | `times_of_day` do medicamento | No horário exato | ❌ Não                  |
| `CONSULTATION_FOLLOWUP`| `follow_up_date`              | 3 dias + dia D   | ✅ Sim                  |
| `GROOMING_DUE`         | `next_date`                   | 2 dias + dia D   | ✅ Sim                  |
| `PET_BIRTHDAY`         | `birth_date` (anual)          | No dia           | ✅ Sim                  |
| `MEDICATION_LATE`      | Dose pendente sem registro    | 30min após       | ❌ Não                  |

**Regras de Negócio:**
- Máximo 5 push/dia por usuário (exceto medicamentos)
- Horário de não perturbe: 22h–7h no timezone do usuário (exceto medicamentos urgentes)
- Notificações in-app expiram após 30 dias
- Usuário pode desativar cada tipo individualmente

---

## Scheduler com Spring

```java
// NotificationScheduler.java
@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;

    // Roda a cada hora — verifica vacinas, consultas e banho/tosa
    @Scheduled(cron = "0 0 * * * *")
    public void checkUpcomingEvents() {
        log.info("job=notification-check status=start");
        try {
            notificationService.scheduleUpcomingNotifications();
            log.info("job=notification-check status=done");
        } catch (Exception e) {
            log.error("job=notification-check status=error", e);
        }
    }

    // Roda a cada 30 minutos — verifica doses atrasadas
    @Scheduled(cron = "0 */30 * * * *")
    public void checkLateMedications() {
        log.info("job=late-medication-check status=start");
        try {
            notificationService.checkLateMedications();
        } catch (Exception e) {
            log.error("job=late-medication-check status=error", e);
        }
    }

    // Roda diariamente às 8h — aniversários dos pets
    @Scheduled(cron = "0 0 8 * * *")
    public void checkPetBirthdays() {
        notificationService.checkPetBirthdays();
    }
}
```

Habilitar no `application.yml`:
```yaml
spring:
  task:
    scheduling:
      enabled: true
```

---

## Produtor RabbitMQ

```java
// NotificationProducer.java
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${petlife.rabbitmq.exchange}")
    private String exchange;

    @Value("${petlife.rabbitmq.routing-key.push}")
    private String pushRoutingKey;

    public void enqueuePushNotification(PushNotificationMessage message) {
        log.info("type=push user_id={} notification_type={}", message.userId(), message.type());
        rabbitTemplate.convertAndSend(exchange, pushRoutingKey, message);
    }
}

// PushNotificationMessage.java
public record PushNotificationMessage(
    UUID userId,
    UUID petId,
    NotificationType type,
    String title,
    String body,
    Map<String, String> data
) {}
```

---

## Consumidor RabbitMQ

```java
// NotificationConsumer.java
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final FcmService fcmService;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @RabbitListener(queues = "${petlife.rabbitmq.queue.push}")
    public void handlePushNotification(PushNotificationMessage message) {
        log.info("consumer=push user_id={} type={}", message.userId(), message.type());

        try {
            var user = userRepository.findById(message.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

            // Verificar horário de não perturbe
            if (!shouldSendNow(message.type(), user.getTimezone())) {
                log.info("consumer=push status=skipped reason=do-not-disturb user_id={}", message.userId());
                return;
            }

            // Verificar limite diário
            if (isOverDailyLimit(message.userId(), message.type())) {
                log.info("consumer=push status=skipped reason=daily-limit user_id={}", message.userId());
                return;
            }

            // Enviar push via FCM
            fcmService.sendToUser(user, message);

            // Salvar in-app
            saveInAppNotification(message, user);

        } catch (Exception e) {
            log.error("consumer=push status=error user_id={}", message.userId(), e);
            throw e; // RabbitMQ fará retry / dead letter
        }
    }

    private boolean shouldSendNow(NotificationType type, String timezone) {
        if (type == NotificationType.MEDICATION_DOSE || type == NotificationType.MEDICATION_LATE) {
            return true; // medicamentos ignoram não perturbe
        }
        var now = ZonedDateTime.now(ZoneId.of(timezone));
        var hour = now.getHour();
        return hour >= 7 && hour < 22;
    }
}
```

---

## Integração FCM (Firebase Admin SDK)

```java
// FcmService.java
@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

    public void sendToUser(User user, PushNotificationMessage message) {
        if (user.getFcmToken() == null) return;

        var notification = Notification.builder()
            .setTitle(message.title())
            .setBody(message.body())
            .build();

        var msg = Message.builder()
            .setToken(user.getFcmToken())
            .setNotification(notification)
            .putAllData(message.data())
            .setAndroidConfig(AndroidConfig.builder()
                .setNotification(AndroidNotification.builder()
                    .setChannelId("petlife-reminders")
                    .setPriority(AndroidNotification.Priority.HIGH)
                    .build())
                .build())
            .setApnsConfig(ApnsConfig.builder()
                .setAps(Aps.builder().setSound("default").setBadge(1).build())
                .build())
            .build();

        try {
            FirebaseMessaging.getInstance().send(msg);
            log.info("fcm=sent user_id={} type={}", user.getId(), message.type());
        } catch (FirebaseMessagingException e) {
            log.error("fcm=error user_id={}", user.getId(), e);
        }
    }
}
```

---

## Lógica de Não Perturbe — Testes TDD

```java
// NotificationSchedulerTest.java
@ExtendWith(MockitoExtension.class)
class NotificationSchedulerTest {

    @InjectMocks
    private NotificationConsumer consumer;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FcmService fcmService;

    @Test
    @DisplayName("Não deve enviar push entre 22h e 7h (não perturbe)")
    void shouldNotSendDuringDoNotDisturbWindow() {
        // simula 23h no timezone do usuário
        var user = UserFactory.make(u -> u.setTimezone("America/Sao_Paulo"));
        var message = new PushNotificationMessage(
            user.getId(), null, NotificationType.VACCINATION_DUE,
            "Vacina próxima", "Luna precisa de reforço", Map.of()
        );

        try (var mockedClock = mockStatic(ZonedDateTime.class)) {
            mockedClock.when(() -> ZonedDateTime.now(any(ZoneId.class)))
                .thenReturn(ZonedDateTime.of(2026, 8, 1, 23, 0, 0, 0, ZoneId.of("America/Sao_Paulo")));

            given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

            consumer.handlePushNotification(message);

            then(fcmService).shouldHaveNoInteractions();
        }
    }

    @Test
    @DisplayName("Deve enviar dose de medicamento mesmo durante não perturbe")
    void shouldSendMedicationDoseDuringDoNotDisturb() {
        var user = UserFactory.make(u -> u.setTimezone("America/Sao_Paulo"));
        var message = new PushNotificationMessage(
            user.getId(), null, NotificationType.MEDICATION_DOSE,
            "Hora do remédio", "Dar 1 comprimido de 50mg", Map.of()
        );

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // mesmo às 23h, medicamento deve ser enviado
        consumer.handlePushNotification(message);

        then(fcmService).should().sendToUser(eq(user), eq(message));
    }
}
```

---

## Configuração RabbitMQ (`application.yml`)

```yaml
petlife:
  rabbitmq:
    exchange: petlife.events
    routing-key:
      push: notification.push
    queue:
      push: petlife.notifications.push
      dlq: petlife.notifications.push.dlq

spring:
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USER:guest}
    password: ${RABBITMQ_PASS:guest}
    listener:
      simple:
        retry:
          enabled: true
          initial-interval: 1000ms
          max-attempts: 3
          multiplier: 2
        default-requeue-rejected: false
```
