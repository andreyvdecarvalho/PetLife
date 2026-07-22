package com.petlife.modules.veterinarian.infrastructure.persistence.mapper;

import com.petlife.modules.auth.infrastructure.persistence.mapper.UserMapper;
import com.petlife.modules.veterinarian.domain.entity.VetAddress;
import com.petlife.modules.veterinarian.domain.entity.VetFavorite;
import com.petlife.modules.veterinarian.domain.entity.VetSchedule;
import com.petlife.modules.veterinarian.domain.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.persistence.entity.VetAddressJpaEntity;
import com.petlife.modules.veterinarian.infrastructure.persistence.entity.VetFavoriteJpaEntity;
import com.petlife.modules.veterinarian.infrastructure.persistence.entity.VetScheduleJpaEntity;
import com.petlife.modules.veterinarian.infrastructure.persistence.entity.VeterinarianJpaEntity;

import java.util.stream.Collectors;

public class VeterinarianMapper {

    public static VeterinarianJpaEntity toJpaEntity(Veterinarian domain) {
        if (domain == null) {
            return null;
        }
        VeterinarianJpaEntity jpa = new VeterinarianJpaEntity();
        jpa.setId(domain.getId());
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        if (domain.getUser() != null) {
            jpa.setUser(UserMapper.toJpaEntity(domain.getUser()));
        }
        jpa.setCrmvNumber(domain.getCrmvNumber());
        jpa.setCrmvState(domain.getCrmvState());
        jpa.setCrmvStatus(domain.getCrmvStatus());
        jpa.setFullName(domain.getFullName());
        jpa.setBio(domain.getBio());
        jpa.setSpecialties(domain.getSpecialties());
        jpa.setSpeciesServed(domain.getSpeciesServed());
        jpa.setModalities(domain.getModalities());
        jpa.setPaymentTypes(domain.getPaymentTypes());
        jpa.setInsurancePlans(domain.getInsurancePlans());
        jpa.setEmergencyOnDuty(domain.isEmergencyOnDuty());
        jpa.setAvailabilityStatus(domain.getAvailabilityStatus());
        jpa.setProfilePhotoUrl(domain.getProfilePhotoUrl());
        jpa.setPhone(domain.getPhone());
        jpa.setWebsiteUrl(domain.getWebsiteUrl());

        return jpa;
    }

    public static Veterinarian toDomain(VeterinarianJpaEntity jpa) {
        if (jpa == null) {
            return null;
        }
        Veterinarian domain = new Veterinarian();
        domain.setId(jpa.getId());
        domain.setCreatedAt(jpa.getCreatedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());
        if (jpa.getUser() != null) {
            domain.setUser(UserMapper.toDomain(jpa.getUser()));
        }
        domain.setCrmvNumber(jpa.getCrmvNumber());
        domain.setCrmvState(jpa.getCrmvState());
        domain.setCrmvStatus(jpa.getCrmvStatus());
        domain.setFullName(jpa.getFullName());
        domain.setBio(jpa.getBio());
        domain.setSpecialties(jpa.getSpecialties());
        domain.setSpeciesServed(jpa.getSpeciesServed());
        domain.setModalities(jpa.getModalities());
        domain.setPaymentTypes(jpa.getPaymentTypes());
        domain.setInsurancePlans(jpa.getInsurancePlans());
        domain.setEmergencyOnDuty(jpa.isEmergencyOnDuty());
        domain.setAvailabilityStatus(jpa.getAvailabilityStatus());
        domain.setProfilePhotoUrl(jpa.getProfilePhotoUrl());
        domain.setPhone(jpa.getPhone());
        domain.setWebsiteUrl(jpa.getWebsiteUrl());
        
        // Map lists
        if (jpa.getAddresses() != null) {
            domain.setAddresses(jpa.getAddresses().stream()
                .map(VeterinarianMapper::toDomain)
                .collect(Collectors.toList()));
        }
        if (jpa.getSchedules() != null) {
            domain.setSchedules(jpa.getSchedules().stream()
                .map(VeterinarianMapper::toDomain)
                .collect(Collectors.toList()));
        }
        if (jpa.getFavorites() != null) {
            domain.setFavorites(jpa.getFavorites().stream()
                .map(VeterinarianMapper::toDomain)
                .collect(Collectors.toList()));
        }

        return domain;
    }

    public static VetAddressJpaEntity toJpaEntity(VetAddress domain) {
        if (domain == null) {
            return null;
        }
        VetAddressJpaEntity jpa = new VetAddressJpaEntity();
        jpa.setId(domain.getId());
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        jpa.setLabel(domain.getLabel());
        jpa.setStreet(domain.getStreet());
        jpa.setNumber(domain.getNumber());
        jpa.setComplement(domain.getComplement());
        jpa.setNeighborhood(domain.getNeighborhood());
        jpa.setCity(domain.getCity());
        jpa.setState(domain.getState());
        jpa.setPostalCode(domain.getPostalCode());
        jpa.setLatitude(domain.getLatitude());
        jpa.setLongitude(domain.getLongitude());
        jpa.setPrimary(domain.isPrimary());
        // We omit back-reference in JpaEntity mapping to prevent infinite loop.
        // Veterinarian must be set manually if needed for save.
        return jpa;
    }

    public static VetAddress toDomain(VetAddressJpaEntity jpa) {
        if (jpa == null) {
            return null;
        }
        VetAddress domain = new VetAddress();
        domain.setId(jpa.getId());
        domain.setCreatedAt(jpa.getCreatedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());
        domain.setLabel(jpa.getLabel());
        domain.setStreet(jpa.getStreet());
        domain.setNumber(jpa.getNumber());
        domain.setComplement(jpa.getComplement());
        domain.setNeighborhood(jpa.getNeighborhood());
        domain.setCity(jpa.getCity());
        domain.setState(jpa.getState());
        domain.setPostalCode(jpa.getPostalCode());
        domain.setLatitude(jpa.getLatitude());
        domain.setLongitude(jpa.getLongitude());
        domain.setPrimary(jpa.isPrimary());
        return domain;
    }

    public static VetScheduleJpaEntity toJpaEntity(VetSchedule domain) {
        if (domain == null) {
            return null;
        }
        VetScheduleJpaEntity jpa = new VetScheduleJpaEntity();
        jpa.setId(domain.getId());
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        jpa.setDayOfWeek(domain.getDayOfWeek());
        jpa.setStartTime(domain.getStartTime());
        jpa.setEndTime(domain.getEndTime());
        jpa.setAvailable(domain.isAvailable());
        return jpa;
    }

    public static VetSchedule toDomain(VetScheduleJpaEntity jpa) {
        if (jpa == null) {
            return null;
        }
        VetSchedule domain = new VetSchedule();
        domain.setId(jpa.getId());
        domain.setCreatedAt(jpa.getCreatedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());
        domain.setDayOfWeek(jpa.getDayOfWeek());
        domain.setStartTime(jpa.getStartTime());
        domain.setEndTime(jpa.getEndTime());
        domain.setAvailable(jpa.isAvailable());
        return domain;
    }

    public static VetFavoriteJpaEntity toJpaEntity(VetFavorite domain) {
        if (domain == null) {
            return null;
        }
        VetFavoriteJpaEntity jpa = new VetFavoriteJpaEntity();
        jpa.setId(domain.getId());
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        if (domain.getUser() != null) {
            jpa.setUser(UserMapper.toJpaEntity(domain.getUser()));
        }
        if (domain.getVeterinarian() != null) {
            jpa.setVeterinarian(toJpaEntity(domain.getVeterinarian()));
        }
        return jpa;
    }

    public static VetFavorite toDomain(VetFavoriteJpaEntity jpa) {
        if (jpa == null) {
            return null;
        }
        VetFavorite domain = new VetFavorite();
        domain.setId(jpa.getId());
        domain.setCreatedAt(jpa.getCreatedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());
        if (jpa.getUser() != null) {
            domain.setUser(UserMapper.toDomain(jpa.getUser()));
        }
        if (jpa.getVeterinarian() != null) {
            domain.setVeterinarian(toDomainShallow(jpa.getVeterinarian()));
        }
        return domain;
    }

    public static Veterinarian toDomainShallow(VeterinarianJpaEntity jpa) {
        if (jpa == null) {
            return null;
        }
        Veterinarian domain = new Veterinarian();
        domain.setId(jpa.getId());
        domain.setCreatedAt(jpa.getCreatedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());
        if (jpa.getUser() != null) {
            domain.setUser(UserMapper.toDomain(jpa.getUser()));
        }
        domain.setCrmvNumber(jpa.getCrmvNumber());
        domain.setCrmvState(jpa.getCrmvState());
        domain.setCrmvStatus(jpa.getCrmvStatus());
        domain.setFullName(jpa.getFullName());
        domain.setBio(jpa.getBio());
        domain.setSpecialties(jpa.getSpecialties());
        domain.setSpeciesServed(jpa.getSpeciesServed());
        domain.setModalities(jpa.getModalities());
        domain.setPaymentTypes(jpa.getPaymentTypes());
        domain.setInsurancePlans(jpa.getInsurancePlans());
        domain.setEmergencyOnDuty(jpa.isEmergencyOnDuty());
        domain.setAvailabilityStatus(jpa.getAvailabilityStatus());
        domain.setProfilePhotoUrl(jpa.getProfilePhotoUrl());
        domain.setPhone(jpa.getPhone());
        domain.setWebsiteUrl(jpa.getWebsiteUrl());
        return domain;
    }
}
