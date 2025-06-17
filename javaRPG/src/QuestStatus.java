public enum QuestStatus {
    NOT_AVAILABLE("수락 불가"), // 아직 조건이 안됨 (예: 선행 퀘스트, 레벨 제한)
    AVAILABLE("수락 가능"),   // NPC에게 받을 수 있는 상태
    ACTIVE("진행 중"),       // 수락하여 진행 중
    COMPLETED("완료 (보고)"),  // 목표 달성, 보고 대기 중
    REWARDED("보상 완료");     // 보상까지 받은 상태

    private final String displayName;

    QuestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
