import java.util.List;
import java.util.ArrayList;

public class Quest {
    private String id; // 퀘스트 고유 ID
    private String title;
    private String description; // 퀘스트 수락 시 설명
    private String objectiveDescription; // 목표 설명 (예: 슬라임 0/5 마리)
    private QuestStatus status;

    // 퀘스트 목표 (다양한 목표를 위해 확장 가능하게 설계)
    private String targetMonsterName;
    private int killCountNeeded;
    private int currentKillCount;

    // 퀘스트 보상
    private int experienceReward;
    private int goldReward;
    private List<ItemQuantity> itemRewards; // 아이템 보상 (아이템과 개수)

    // 수락 조건 (선택 사항)
    private int requiredLevel;
    // private String prerequisiteQuestId;

    public Quest(String id, String title, String description, String objectiveDescription,
                 String targetMonsterName, int killCountNeeded,
                 int experienceReward, int goldReward, List<ItemQuantity> itemRewards,
                 int requiredLevel) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.objectiveDescription = objectiveDescription; // 초기 목표 설명
        this.targetMonsterName = targetMonsterName;
        this.killCountNeeded = killCountNeeded;
        this.experienceReward = experienceReward;
        this.goldReward = goldReward;
        this.itemRewards = itemRewards != null ? itemRewards : new ArrayList<>();
        this.requiredLevel = requiredLevel;

        this.status = QuestStatus.NOT_AVAILABLE; // 초기 상태
        this.currentKillCount = 0;
    }

    // Getter들
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public QuestStatus getStatus() { return status; }
    public String getTargetMonsterName() { return targetMonsterName; }
    public int getKillCountNeeded() { return killCountNeeded; }
    public int getCurrentKillCount() { return currentKillCount; }
    public int getExperienceReward() { return experienceReward; }
    public int getGoldReward() { return goldReward; }
    public List<ItemQuantity> getItemRewards() { return itemRewards; }
    public int getRequiredLevel() { return requiredLevel; }


    // 퀘스트 수락 가능 여부 확인
    public boolean canAccept(Player player) {
        return player.getLevel() >= this.requiredLevel && this.status == QuestStatus.AVAILABLE;
    }

    // 퀘스트 상태 변경
    public void setStatus(QuestStatus status) { this.status = status; }


    // 몬스터 처치 시 진행도 업데이트
    public boolean updateProgress(String monsterKilledName) {
        if (status == QuestStatus.ACTIVE && monsterKilledName.equals(targetMonsterName)) {
            currentKillCount++;
            if (currentKillCount >= killCountNeeded) {
                currentKillCount = killCountNeeded; // 초과 방지
                setStatus(QuestStatus.COMPLETED);
                return true; // 목표 달성
            }
            return false; // 진행 중
        }
        return false; // 해당사항 없음
    }

    // 퀘스트 보상 받기
    public boolean claimReward(Player player, GameManager gameManager) { // GameManager 참조 추가 (로그용)
        if (status == QuestStatus.COMPLETED) {
            player.gainExp(experienceReward);
            // player.addGold(goldReward); // 골드 시스템 구현 시 GameManager에 요청
            gameManager.addPlayerGold(goldReward); // 예시

            for (ItemQuantity iq : itemRewards) {
                player.getInventory().addItem(iq.getItem(), iq.getQuantity());
            }
            setStatus(QuestStatus.REWARDED);

            if (gameManager.getLogPanel() != null) {
                gameManager.getLogPanel().addLog("퀘스트 완료! [" + title + "] 보상을 받았습니다.");
                gameManager.getLogPanel().addLog("경험치 +" + experienceReward + ", 골드 +" + goldReward);
                for (ItemQuantity iq : itemRewards) {
                    gameManager.getLogPanel().addLog("아이템: " + iq.getItem().getName() + " x" + iq.getQuantity());
                }
            }
            return true;
        }
        return false;
    }

    // 현재 퀘스트 진행 상황 문자열 (UI 표시용)
    public String getProgressString() {
        if (targetMonsterName != null) { // 몬스터 처치 퀘스트인 경우
            return String.format("%s: %s (%d/%d)", title, objectiveDescription, currentKillCount, killCountNeeded);
        }
        return title + ": " + objectiveDescription; // 다른 종류의 퀘스트
    }
}
