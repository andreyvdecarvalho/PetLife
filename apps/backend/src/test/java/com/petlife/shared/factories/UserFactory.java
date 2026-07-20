package com.petlife.shared.factories;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.auth.domain.entity.UserPlan;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.function.Consumer;

public class UserFactory {

    private static final Faker faker = new Faker(Locale.of("pt", "BR"));

    public static User make() {
        return make(u -> {});
    }

    public static User make(Consumer<User> overrides) {
        User user = new User();
        user.setName(faker.name().fullName());
        user.setEmail(faker.internet().emailAddress());
        // Hash de senha BCrypt para "Senha@123"
        user.setPasswordHash("$2a$12$N9qo8uLOpvIAwy10B74dOeO6L/X5O29.eLg70.Xj0m2.q.Uv2hI2S");
        user.setPlan(UserPlan.FREE);
        user.setEmailVerified(true);
        user.setAvatarUrl(faker.internet().image());
        user.setTimezone(com.petlife.modules.auth.domain.entity.Timezone.AMERICA_SAO_PAULO);
        user.setLgpdAcceptedAt(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        overrides.accept(user);
        return user;
    }
}
