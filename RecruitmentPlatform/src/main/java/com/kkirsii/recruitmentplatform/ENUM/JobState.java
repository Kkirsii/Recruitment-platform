package com.kkirsii.recruitmentplatform.ENUM;

public enum JobState {
    SUBMIT_SUCCESS(0),
    FIRST_PASS(1),
    TECH_PASS(2),
    MANAGER_PASS(3),
    SORTING(4);

    private final int code;

    JobState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static JobState fromCode(int code) {
        for (JobState state : JobState.values()) {
            if (state.getCode() == code) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid JobState code: " + code);
    }
}
