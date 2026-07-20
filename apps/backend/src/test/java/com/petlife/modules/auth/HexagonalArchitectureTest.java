package com.petlife.modules.auth;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexagonalArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.petlife.modules.auth");

    @Test
    void domainShouldNotDependOnInfrastructureOrSpring() {
        noClasses()
                .that().resideInAPackage("..auth.domain..")
                .should().dependOnClassesThat().resideInAPackage("..auth.infrastructure..")
                .orShould().dependOnClassesThat().resideInAPackage("org.springframework..")
                .check(classes);
    }

    @Test
    void applicationShouldNotDependOnInfrastructureOrSpring() {
        noClasses()
                .that().resideInAPackage("..auth.application..")
                .should().dependOnClassesThat().resideInAPackage("..auth.infrastructure..")
                .orShould().dependOnClassesThat().resideInAPackage("org.springframework..")
                .check(classes);
    }
}
