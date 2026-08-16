package it.ispwproject.doseguard.enumerator;

public enum Role {
        PATIENT,
        DOCTOR,
        PHARMACIST;

        public static Role fromString(String role) {
            return switch (role.toUpperCase()) {
                case "PATIENT" -> PATIENT;
                case "DOCTOR" -> DOCTOR;
                case "PHARMACIST" -> PHARMACIST;
                default -> throw new IllegalArgumentException(
                    "Ruolo non valido: " + role);
        };
    }
}
