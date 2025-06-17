import javax.swing.*;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class GameManager {

    private Player player;
    private Monster currentMonster;
    private LogPanel logPanelRef;
    private CharacterDisplayPanel characterDisplayPanelRef;
    private ButtonPanel buttonPanelRef;
    private GameWindow gameWindowRef;
    private Random randomGenerator = new Random();

    public enum Location { TOWN, SLIME_CAVE, GOBLIN_FOREST, DRAGON_LAIR }
    private Location currentLocation;

    private static class MonsterData {
        String name; int maxHp; int attackPower; int expGiven; String imagePath;
        public MonsterData(String n, int h, int a, int e, String img) {
            name=n; maxHp=h; attackPower=a; expGiven=e; imagePath=img;
        }
    }
    private Map<Location, MonsterData[]> huntingGroundMonsters;

    public enum PlayerAction {
        ATTACK, HEAL,
        GO_TO_SLIME_CAVE, GO_TO_GOBLIN_FOREST, GO_TO_DRAGON_LAIR,
        RETURN_TO_TOWN, REST_IN_TOWN,
        OPEN_INVENTORY, OPEN_EQUIPMENT, OPEN_QUEST_LOG, OPEN_SHOP, OPEN_STATUS
    }

    private Map<String, Item> allItemsDatabase;
    private List<Item> shopInventory;
    private Map<String, Quest> allQuestsDatabase; // 모든 퀘스트 정의 저장
    private CharacterStatusDialog characterStatusDialogInstance;

    public GameManager() {
        player = new Player("용사", 100, 50, 15);
        currentLocation = Location.TOWN;
        initializeItemsAndShop();
        initializeQuests(); // 퀘스트 데이터 초기화
        initializeHuntingGrounds();
    }

    private void initializeItemsAndShop() {
        allItemsDatabase = new HashMap<>();
        shopInventory = new ArrayList<>();
        Equipment basicSword = new Equipment("낡은 검", "녹슨 기본적인 검.", 20, EquipmentSlot.WEAPON, 3, 0, 0, 0);
        Equipment leatherArmor = new Equipment("가죽 갑옷", "사냥꾼이 쓰던 가죽 갑옷.", 30, EquipmentSlot.ARMOR_BODY, 0, 2, 5, 0);
        LootItem slimeJelly = new LootItem("끈적한 점액", "슬라임의 전리품. 약간 미끌거린다.", 2);
        LootItem goblinCloth = new LootItem("고블린 천조각", "고블린의 누더기 옷에서 떼어냈다.", 5);
        LootItem dragonScale = new LootItem("드래곤 비늘", "용의 단단한 비늘. 귀한 재료다.", 50);
        allItemsDatabase.put(basicSword.getName(), basicSword);
        allItemsDatabase.put(leatherArmor.getName(), leatherArmor);
        allItemsDatabase.put(slimeJelly.getName(), slimeJelly);
        allItemsDatabase.put(goblinCloth.getName(), goblinCloth);
        allItemsDatabase.put(dragonScale.getName(), dragonScale);
        shopInventory.add(allItemsDatabase.get("낡은 검"));
        shopInventory.add(allItemsDatabase.get("가죽 갑옷"));
    }

    private void initializeQuests() {
        allQuestsDatabase = new HashMap<>(); // Quest 객체를 저장할 Map

        // 슬라임 퀘스트 보상 아이템 리스트 생성
        List<ItemQuantity> slimeQuestRewards = new ArrayList<>();
        Item slimeJellyForQuest = allItemsDatabase.get("끈적한 점액");
        if (slimeJellyForQuest != null) {
            slimeQuestRewards.add(new ItemQuantity(slimeJellyForQuest, 3));
        }

        // 슬라임 퀘스트 객체 생성
        Quest slimeQuest = new Quest(
                "Q001",                                  // ID
                "마을 주변 정리",                          // 제목
                "마을 근처에 슬라임이 너무 많아졌습니다. 주민들을 위해 3마리만 처치해주세요.", // 설명
                "슬라임 처치하기",                         // 목표 설명 (UI 표시용)
                "슬라임",                                // 목표 몬스터 이름
                3,                                       // 필요한 처치 수
                50,                                      // 경험치 보상
                20,                                      // 골드 보상
                slimeQuestRewards,                       // 아이템 보상 리스트
                1                                        // 요구 레벨
        );
        allQuestsDatabase.put(slimeQuest.getId(), slimeQuest);


        // 고블린 퀘스트 보상 아이템 리스트 생성
        List<ItemQuantity> goblinQuestRewards = new ArrayList<>();
        Item goblinClothForQuest = allItemsDatabase.get("고블린 천조각");
        if (goblinClothForQuest != null) {
            goblinQuestRewards.add(new ItemQuantity(goblinClothForQuest, 2));
        }

        // 고블린 퀘스트 객체 생성
        Quest goblinQuest = new Quest(
                "Q002",                                  // ID
                "숲의 성가신 존재들",                       // 제목
                "숲길을 지나려는 상인들이 고블린 때문에 고통받고 있습니다. 녀석들을 혼내주세요.", // 설명
                "고블린 처치하기",                         // 목표 설명
                "고블린",                                // 목표 몬스터 이름
                2,                                       // 필요한 처치 수
                120,                                     // 경험치 보상
                50,                                      // 골드 보상
                goblinQuestRewards,                      // 아이템 보상 리스트
                3                                        // 요구 레벨
        );
        allQuestsDatabase.put(goblinQuest.getId(), goblinQuest);
    }

    private void initializeHuntingGrounds() {
        huntingGroundMonsters = new HashMap<>();
        huntingGroundMonsters.put(Location.SLIME_CAVE, new MonsterData[]{ new MonsterData("슬라임", 30, 5, 10, "/images/slime_stand.png"), });
        huntingGroundMonsters.put(Location.GOBLIN_FOREST, new MonsterData[]{ new MonsterData("고블린", 50, 8, 20, "/images/goblin_stand.png"), });
        huntingGroundMonsters.put(Location.DRAGON_LAIR, new MonsterData[]{ new MonsterData("드래곤", 150, 20, 100, "/images/dragon_stand.png"), });
    }

    public void setLogPanel(LogPanel lp) { this.logPanelRef = lp; }
    public void setCharacterDisplayPanel(CharacterDisplayPanel cdp) { this.characterDisplayPanelRef = cdp; }
    public void setButtonPanel(ButtonPanel bp) { this.buttonPanelRef = bp; }
    public void setGameWindow(GameWindow gw) { this.gameWindowRef = gw; }

    public Player getPlayer() { return player; }
    public Monster getCurrentMonster() { return currentMonster; }
    public Location getCurrentLocation() { return currentLocation; }
    public LogPanel getLogPanel() { return logPanelRef; }
    public List<Item> getShopInventory() { return new ArrayList<>(shopInventory); }
    public Map<String, Quest> getAllQuests() { return new HashMap<>(allQuestsDatabase); } // 모든 퀘스트 정의 반환

    public void startGame() {
        log("게임을 시작합니다! 현재 위치: " + getLocationName(currentLocation));
        currentMonster = null;
        updateAllQuestsAvailability(); // 게임 시작 시 플레이어 레벨에 따라 퀘스트 수락 가능 상태 업데이트
        if (characterDisplayPanelRef != null) characterDisplayPanelRef.updateBackground(currentLocation);
        updateUI();
    }

    private void spawnNewMonster() {
        if (!isHuntingLocation(currentLocation)) { currentMonster = null; return; }
        MonsterData[] monstersInLoc = huntingGroundMonsters.get(currentLocation);
        if (monstersInLoc != null && monstersInLoc.length > 0) {
            MonsterData selected = monstersInLoc[randomGenerator.nextInt(monstersInLoc.length)];
            currentMonster = new Monster(selected.name, selected.maxHp, selected.attackPower, selected.expGiven, selected.imagePath);
            log("야생의 " + currentMonster.getName() + " (이)가 나타났다!");
        } else {
            log(getLocationName(currentLocation) + "에는 더 이상 몬스터가 없습니다!");
            currentMonster = null;
        }
    }

    private boolean isHuntingLocation(Location loc) { return huntingGroundMonsters.containsKey(loc); }

    public void handlePlayerAction(PlayerAction action) {
        if (!player.isAlive() && action != PlayerAction.RETURN_TO_TOWN) {
            log("플레이어가 쓰러져 행동할 수 없습니다.");
            if (currentLocation != Location.TOWN && buttonPanelRef != null) buttonPanelRef.updateButtonStatesForDefeat();
            return;
        }

        boolean playerTookTurn = false;
        switch (action) {
            case ATTACK:
                if (currentMonster != null && currentMonster.isAlive() && player.isAlive()) {
                    if (gameWindowRef != null) gameWindowRef.playPlayerAttackAnimation();
                    else { log("UI 참조 오류로 애니메이션 없이 즉시 공격합니다."); processActualAttack(); }
                } else {
                    log("공격할 대상이 없거나 플레이어가 행동할 수 없습니다.");
                    if (isHuntingLocation(currentLocation) && (currentMonster == null || !currentMonster.isAlive())) spawnNewMonster();
                }
                break;
            case HEAL: playerHeal(); playerTookTurn = true; break;
            case GO_TO_SLIME_CAVE: moveToLocation(Location.SLIME_CAVE); break;
            case GO_TO_GOBLIN_FOREST: moveToLocation(Location.GOBLIN_FOREST); break;
            case GO_TO_DRAGON_LAIR: moveToLocation(Location.DRAGON_LAIR); break;
            case RETURN_TO_TOWN: moveToLocation(Location.TOWN); break;
            case REST_IN_TOWN:
                if (currentLocation == Location.TOWN) {
                    boolean wasDead = !player.isAlive(); player.restFully();
                    log(player.getName() + "이(가) 마을에서 휴식을 취해 모든 HP와 MP를 회복했습니다.");
                    if(wasDead && player.isAlive()) log(player.getName() + "이(가) 기력을 되찾았습니다!");
                } else { log("마을에서만 휴식할 수 있습니다."); }
                playerTookTurn = true;
                break;
            case OPEN_INVENTORY:
                if (gameWindowRef != null) new InventoryDialog(gameWindowRef, player, this).setVisible(true);
                else log("인벤토리 UI를 열 수 없습니다.");
                break;
            case OPEN_EQUIPMENT:
                if (gameWindowRef != null) { log("장비 관리는 가방에서 할 수 있습니다."); new InventoryDialog(gameWindowRef, player, this).setVisible(true); }
                else log("장비 UI를 열 수 없습니다.");
                break;
            case OPEN_QUEST_LOG:
                if (gameWindowRef != null) openQuestLogViaJOptionPane();
                else log("퀘스트 UI를 열 수 없습니다.");
                break;
            case OPEN_SHOP:
                if (currentLocation == Location.TOWN) {
                    if (gameWindowRef != null) new ShopDialog(gameWindowRef, this).setVisible(true);
                    else log("상점 UI를 열 수 없습니다.");
                } else { log("상점은 마을에서만 이용할 수 있습니다."); }
                break;
            case OPEN_STATUS:
                if (gameWindowRef != null) {
                    if (characterStatusDialogInstance == null) characterStatusDialogInstance = new CharacterStatusDialog(gameWindowRef, player);
                    characterStatusDialogInstance.setPlayer(player);
                    characterStatusDialogInstance.setVisible(true);
                } else { log("캐릭터 정보 창을 열 수 없습니다.");}
                break;
        }

        if (playerTookTurn && action != PlayerAction.ATTACK) {
            if (currentMonster != null && currentMonster.isAlive() && player.isAlive() && isHuntingLocation(currentLocation)) monsterAttack();
            if (isHuntingLocation(currentLocation) || !player.isAlive()) checkBattleEnd();
        }
        updateAllQuestsAvailability();
        if (action != PlayerAction.ATTACK && !isLocationChangeAction(action)) updateUI();
    }

    private boolean isLocationChangeAction(PlayerAction action) {
        return action == PlayerAction.GO_TO_SLIME_CAVE || action == PlayerAction.GO_TO_GOBLIN_FOREST ||
                action == PlayerAction.GO_TO_DRAGON_LAIR || action == PlayerAction.RETURN_TO_TOWN;
    }

    private void moveToLocation(Location newLocation) {
        log(getLocationName(newLocation) + " (으)로 이동합니다...");
        currentLocation = newLocation; currentMonster = null;
        if (characterDisplayPanelRef != null) characterDisplayPanelRef.updateBackground(currentLocation);
        if (isHuntingLocation(newLocation)) spawnNewMonster();
        updateUI();
    }

    public void processActualAttack() {
        boolean monsterWasAlive = (currentMonster != null && currentMonster.isAlive());
        if (!monsterWasAlive || !player.isAlive()) {
            if (monsterWasAlive) log("공격 처리 중 대상이 사라졌거나 플레이어가 행동 불능입니다.");
            else if (isHuntingLocation(currentLocation)) { log("공격할 몬스터가 없습니다. 새로운 몬스터를 찾습니다..."); spawnNewMonster(); }
            updateUI(); return;
        }

        int damageDealt = player.getTotalAttack(); currentMonster.takeDamage(damageDealt);
        log(player.getName() + "의 공격! " + currentMonster.getName() + "에게 " + damageDealt + "의 데미지!");

        if (!currentMonster.isAlive()) {
            log(currentMonster.getName() + "을(를) 쓰러뜨렸다!");
            List<ItemQuantity> loot = currentMonster.dropLoot();
            if (!loot.isEmpty()) { log("--- 전리품 획득 ---");
                for (ItemQuantity iq : loot) { player.getInventory().addItem(iq.getItem(), iq.getQuantity()); log(iq.getItem().getName() + " x" + iq.getQuantity()); }
                log("------------------"); }
            for (Quest quest : new ArrayList<>(player.getActiveQuests())) if(quest.updateProgress(currentMonster.getName())) log("퀘스트 목표 달성: " + quest.getTitle() + " (마을에서 보고하세요)");
            int oldLevel = player.getLevel(); player.gainExp(currentMonster.getExpGiven());
            if (player.getLevel() > oldLevel) log(player.getName()+" 레벨 업! -> Lv."+player.getLevel()+" | HP: "+player.getMaxHp()+", MP: "+player.getMaxMp()+", ATK: "+player.getTotalAttack()+", DEF: "+player.getTotalDefense());
        }

        if (currentMonster != null && currentMonster.isAlive() && player.isAlive() && isHuntingLocation(currentLocation)) monsterAttack();
        updateUI(); checkBattleEnd();
    }

    private void playerHeal() {
        int heal = 30, cost = 10;
        if (player.useMp(cost)) { player.healHp(heal); log(player.getName()+"이(가) MP "+cost+"를 소모하여 HP "+heal+"를 회복!");
        } else { log("MP가 부족하여 회복할 수 없습니다!");}
    }

    private void monsterAttack() {
        int damage = currentMonster.getAttack(); player.takeDamage(damage);
        log(currentMonster.getName()+"의 반격! "+player.getName()+"은(는) 피해를 입었다.");
        if (!player.isAlive()) log(player.getName() + "은(는) 쓰러졌다... GAME OVER");
    }

    private void checkBattleEnd() {
        if (!player.isAlive()) { if (buttonPanelRef != null) buttonPanelRef.updateButtonStatesForDefeat();
        } else if (currentMonster != null && !currentMonster.isAlive()) {
            log(currentMonster.getName() + "과의 전투에서 승리했습니다!");
            if (isHuntingLocation(currentLocation)) spawnNewMonster(); else currentMonster = null;
        }
    }

    public void updateUI() {
        if (characterDisplayPanelRef != null) {
            characterDisplayPanelRef.updatePlayerDisplay(player);
            characterDisplayPanelRef.updateMonsterDisplay(currentMonster);
        }
        if (buttonPanelRef != null) buttonPanelRef.updateButtonsForLocation(currentLocation, player.isAlive());
        if (characterStatusDialogInstance != null && characterStatusDialogInstance.isVisible()) characterStatusDialogInstance.setPlayer(player);
    }

    private String getLocationName(Location loc) {
        if (loc == null) return "알 수 없는 장소";
        switch (loc) { case TOWN: return "마을"; case SLIME_CAVE: return "슬라임 동굴"; case GOBLIN_FOREST: return "고블린 숲"; case DRAGON_LAIR: return "드래곤 둥지"; default: return loc.toString(); }
    }

    public void addPlayerGold(int amount) { player.addGold(amount); log(amount + " 골드를 획득했습니다. (현재 골드: " + player.getGold() + ")");}

    public boolean buyItemFromShop(String itemName) {
        Item itemToBuy = shopInventory.stream().filter(i -> i.getName().equals(itemName)).findFirst().orElse(null);
        if (itemToBuy == null) { log(itemName + " 아이템은 상점에서 판매하지 않습니다."); return false; }
        if (player.spendGold(itemToBuy.getValue())) {
            Item boughtItem;
            if (itemToBuy instanceof Equipment) { Equipment eq = (Equipment) itemToBuy; boughtItem = new Equipment(eq.getName(),eq.getDescription(),eq.getValue(),eq.getSlot(),eq.getAttackBonus(),eq.getDefenseBonus(),eq.getHpBonus(),eq.getMpBonus());}
            else { boughtItem = new Item(itemToBuy.getName(), itemToBuy.getDescription(), itemToBuy.getValue(), itemToBuy.isStackable());}
            player.getInventory().addItem(boughtItem, 1); log(boughtItem.getName() + " 을(를) 구매했습니다.");
            updateUI(); return true;
        } else { log("골드가 부족하여 " + itemName + "을(를) 구매할 수 없습니다."); return false; }
    }

    public boolean sellItemToShop(Item item, int quantity) {
        if(player.getInventory().getQuantity(item) < quantity){ log(item.getName() + " 아이템이 " + quantity + "개 미만이라 판매 불가."); return false; }
        if (player.getInventory().removeItem(item, quantity)) {
            int gold = (item.getValue() / 2) * quantity; if (gold < 1 && item.getValue() >= 1) gold = 1 * quantity;
            player.addGold(gold); log(item.getName() + " " + quantity + "개를 팔아 " + gold + " 골드를 얻었습니다.");
            updateUI(); return true;
        } else { log(item.getName() + " 판매 실패."); return false; }
    }

    public void updateAllQuestsAvailability() { for (Quest q : allQuestsDatabase.values()) if (q.getStatus()==QuestStatus.NOT_AVAILABLE && player.getLevel()>=q.getRequiredLevel()) q.setStatus(QuestStatus.AVAILABLE); }
    public List<Quest> getAcceptableQuests() { return allQuestsDatabase.values().stream().filter(q->q.getStatus()==QuestStatus.AVAILABLE && q.canAccept(player) && player.getActiveQuests().stream().noneMatch(aq->aq.getId().equals(q.getId()))).collect(Collectors.toList()); }
    public void acceptQuest(String id) {
        Quest q = allQuestsDatabase.get(id);
        if (q!=null && q.getStatus()==QuestStatus.AVAILABLE && q.canAccept(player) && player.getActiveQuests().stream().noneMatch(aq->aq.getId().equals(id))) {
            q.setStatus(QuestStatus.ACTIVE); player.addQuest(q); log("퀘스트 수락: ["+q.getTitle()+"]\n목표: "+q.getProgressString());
        } else if (q!=null && player.getActiveQuests().stream().anyMatch(aq->aq.getId().equals(id))) log("이미 진행 중이거나 완료한 퀘스트: "+q.getTitle());
        else log("해당 퀘스트를 수락할 수 없습니다.");
        updateUI();
    }
    public List<Quest> getCompletableQuests() { return player.getActiveQuests().stream().filter(q->q.getStatus()==QuestStatus.COMPLETED).collect(Collectors.toList()); }
    public void completeQuestAndGetReward(String id) {
        Quest qtc = player.getActiveQuests().stream().filter(q->q.getId().equals(id) && q.getStatus()==QuestStatus.COMPLETED).findFirst().orElse(null);
        if (qtc!=null) qtc.claimReward(player, this);
        else log("해당 퀘스트를 완료할 수 없거나 이미 보상을 받았습니다.");
        updateUI();
    }
    private void openQuestLogViaJOptionPane() {
        JFrame parent = (gameWindowRef != null) ? gameWindowRef : null; StringBuilder sb = new StringBuilder();
        sb.append("--- 진행 중 / 완료 (보고 전) 퀘스트 ---\n");
        List<Quest> current = player.getActiveQuests().stream().filter(q->q.getStatus()==QuestStatus.ACTIVE || q.getStatus()==QuestStatus.COMPLETED).collect(Collectors.toList());
        if (current.isEmpty()) sb.append("없음\n"); else for (int i=0;i<current.size();i++) sb.append((i+1)+".["+current.get(i).getStatus().getDisplayName()+"]"+current.get(i).getProgressString()+"\n");
        sb.append("\n--- 수락 가능한 퀘스트 ---\n"); List<Quest> acceptable = getAcceptableQuests();
        if (acceptable.isEmpty()) sb.append("없음\n"); else for (int i=0;i<acceptable.size();i++) sb.append((current.size()+i+1)+".(수락)"+acceptable.get(i).getTitle()+"(Lv."+acceptable.get(i).getRequiredLevel()+")\n");
        sb.append("\n---------------------------\n번호 입력 (예: 1 또는 " + (current.size()+1) + ", 취소는 빈칸):"); String input = JOptionPane.showInputDialog(parent, sb.toString(), "퀘스트 목록", JOptionPane.PLAIN_MESSAGE);
        if (input!=null && !input.trim().isEmpty()) { try { int sel = Integer.parseInt(input.trim()); int curOffset = current.size();
            if (sel>0 && sel<=curOffset) { Quest sQuest=current.get(sel-1); if(sQuest.getStatus()==QuestStatus.COMPLETED) completeQuestAndGetReward(sQuest.getId()); else JOptionPane.showMessageDialog(parent, "["+sQuest.getTitle()+"] 미완료.", "알림", JOptionPane.INFORMATION_MESSAGE);
            } else if (sel>curOffset && sel<=curOffset+acceptable.size()) acceptQuest(acceptable.get(sel-curOffset-1).getId());
            else JOptionPane.showMessageDialog(parent, "잘못된 번호.", "오류", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) { JOptionPane.showMessageDialog(parent, "숫자 입력.", "오류", JOptionPane.ERROR_MESSAGE);}}
    }

    public void log(String message) {
        if (logPanelRef != null) {
            logPanelRef.addLog(message);
        } else {
            System.out.println("LOG (logPanelRef is null): " + message);
        }
    }
}