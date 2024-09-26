package io.github.skippyall.servermoney.money;

import io.github.skippyall.servermoney.ServerMoney;
import io.github.skippyall.servermoney.compat.minions.MinionsCompat;
import io.github.skippyall.servermoney.config.ServerMoneyConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MoneyStorage extends PersistentState {
    public static final Type<MoneyStorage> TYPE = new Type<>(MoneyStorage::new, MoneyStorage::createFromNbt, null);
    private static final HashMap<UUID, Double> moneymap = new HashMap<>();

    private static MoneyStorage INSTANCE;

    public static double getMoney(PlayerEntity player){
        return getMoney(player.getGameProfile().getId());
    }

    public static double getMoney(UUID id){
        return moneymap.containsKey(id) ? moneymap.get(id) : getInitialMoney(id);
    }

    public static double getInitialMoney(UUID id) {
        return MinionsCompat.isMinion(id) ? 0 : ServerMoneyConfig.initialMoney;
    }

    public static void setMoney(PlayerEntity player, double money){
        setMoney(player.getGameProfile().getId(), money);
    }

    public static void setMoney(@NotNull UUID id, double money){
        moneymap.put(id, money);
    }

    public static double addMoney(PlayerEntity player, double money){
        return addMoney(player.getGameProfile().getId(), money);
    }

    public static double addMoney(@NotNull UUID id, double money){
        double newMoney = getMoney(id) + money;
        setMoney(id, newMoney);
        return newMoney;
    }

    public static boolean tryRemoveMoney(PlayerEntity player, double money){
        return tryRemoveMoney(player.getGameProfile().getId(), money);
    }

    public static boolean tryRemoveMoney(@NotNull UUID id, double money){
        if(canPay(id, money)) {
            addMoney(id, -money);
            return true;
        } else {
            return false;
        }
    }

    public static boolean tryPay(UUID sender, UUID receiver, double money) {
        if(canPay(sender, money)) {
            double senderMoney = getMoney(sender);
            setMoney(sender, senderMoney - money);

            double receiverMoney = getMoney(receiver);
            setMoney(receiver, receiverMoney + money);
            return true;
        }
        return false;
    }

    public static boolean canPay(PlayerEntity player, double money) {
        return canPay(player.getGameProfile().getId(), money);
    }

    public static boolean canPay(UUID player, double money) {
        return getMoney(player) >= money;
    }

    private static final String ID_KEY = "id";
    private static final String MONEY_KEY = "money";

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        NbtList money = new NbtList();
        for(Map.Entry<UUID, Double> entry : moneymap.entrySet()){
            NbtCompound compound = new NbtCompound();
            compound.putUuid(ID_KEY, entry.getKey());
            compound.putDouble(MONEY_KEY, entry.getValue());
            money.add(compound);
        }
        nbt.put(MONEY_KEY, money);
        return nbt;
    }

    public static MoneyStorage createFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        NbtList money = (NbtList) nbt.get("money");
        if(money!=null) {
            for (NbtElement element : money) {
                NbtCompound compound = (NbtCompound) element;
                moneymap.put(compound.getUuid(ID_KEY), compound.getDouble(MONEY_KEY));
            }
        }
        return new MoneyStorage();
    }

    public static void init(MinecraftServer server){
        PersistentStateManager manager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        INSTANCE = manager.getOrCreate(TYPE, ServerMoney.MOD_ID);
        INSTANCE.markDirty();
    }
}
