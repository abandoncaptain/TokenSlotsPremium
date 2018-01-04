package me.abandoncaptian.TokenSlots;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;

public class Machine {
	private Player p;
	private int price;
	private boolean active;
	private int reward;
	private Inventory inv;
	private Main pl;
	private DataBase db;
	private EcoMan eco;
	private HashMap<String, BukkitTask> tasks = new HashMap<String, BukkitTask>();
	private int result = 0;

	public Machine(Player p, int price){
		super();
		this.p = p;
		this.price = price;
		this.reward = 0;
		this.pl = Main.getMain();
		this.db = DataBase.getDB();
		this.eco = EcoMan.getEM();
		this.result = 0;
	}

	public Machine(Player p){
		super();
		this.p = p;
		this.price = 0;
		this.reward = 0;
		this.pl = Main.getMain();
		this.result = 0;
	}

	public void setActive(boolean active){
		this.active = active;
	}

	public boolean getActive(){
		return this.active;
	}

	public int getPrice(){
		return this.price;
	}

	public Player getPlayer(){
		return this.p;
	}

	@SuppressWarnings("unused")
	private void setReward(int reward){
		this.reward = reward;
	}

	public int getReward(){
		return this.reward;
	}

	public Inventory createInventory(){
		Inventory inv = Bukkit.createInventory(null, 45, "§7§l[§b§lSlots§7§l]");
		this.inv = inv;
		invSpaceFill();
		openInv();
		return inv;
	}

	public void invSpaceFill(){
		this.active = true;
		this.inv.clear();
		ItemStack holder = pl.filler;
		holder = addItem(holder, " ");
		for(int num = 0; num <= 45; num++)
		{
			if(num < 11){
				this.inv.setItem(num, holder);
			}else if(num == 12){
				this.inv.setItem(num, holder);
			}else if(num == 14){
				this.inv.setItem(num, holder);
			}else if(num > 15 && num < 20){
				this.inv.setItem(num, holder);
			}else if(num == 21){
				this.inv.setItem(num, holder);
			}else if(num == 23){
				this.inv.setItem(num, holder);
			}else if(num > 24 && num < 29){
				this.inv.setItem(num, holder);
			}else if(num == 30){
				this.inv.setItem(num, holder);
			}else if(num == 32){
				this.inv.setItem(num, holder);
			}else if(num > 33 && num < 45){
				this.inv.setItem(num, holder);
			}
		}
	}

	public void openInv(){
		this.p.openInventory(this.inv);
	}

	public void Cancel(){

	}

	public void Run(){
		columTask(3, 0, false, 11, 20, 29, "c1");
		columTask(6, 40, false, 11, 20, 29, "c1");
		columTask(10, 80, true, 11, 20, 29, "c1");

		columTask(3, 0, false, 13, 22, 31, "c2");
		columTask(6, 80, false, 13, 22, 31, "c2");
		columTask(10, 120, true, 13, 22, 31, "c2");

		columTask(3, 0, false, 15, 24, 33, "c3");
		columTask(6, 120, false, 15, 24, 33, "c3");
		columTask(10, 160, true, 15, 24, 33, "c3");
		Bukkit.getScheduler().runTaskLater(pl, () -> {
			if(getActive()){
				this.result = columCheck(20, 22, -1);
				if(this.result == 2){
					this.inv.setItem(18, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
					this.inv.setItem(19, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
					this.inv.setItem(21, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
				}}
		}, 120);
		Bukkit.getScheduler().runTaskLater(pl, () -> {
			if(getActive()){
				if(this.result == 2)this.result = columCheck(20, 22, 24);
				if(this.result == 3){
					this.inv.setItem(23, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
					this.inv.setItem(25, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
					this.inv.setItem(26, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
				}
				if((this.result == 3) || (this.result == 2)){
					if(this.inv.getItem(20).getType() != Material.PAPER){
						this.reward = winningCal(this.inv.getItem(20));
					}else if(this.inv.getItem(22).getType() != Material.PAPER){
						this.reward = winningCal(this.inv.getItem(22));
					}else{
						this.reward = winningCal(this.inv.getItem(24));
					}
					this.reward = (int) (getReward() - (pl.winTax * getReward()));
					String connnected = pl.Connected;
					connnected = connnected.replaceAll("%ConnectedAmount%", String.valueOf(this.result));
					getPlayer().sendMessage(pl.ChatPrefix + connnected);
					if(pl.jackpotSystem){
						int jackpot = eco.getJackpot();
						eco.resetJackpot();
						int output = (getReward() + jackpot);
						String youWon = pl.YouWonWithJackpot;
						youWon = youWon.replaceAll("%Reward%", String.valueOf(getReward()));
						youWon = youWon.replaceAll("%Jackpot%", String.valueOf(jackpot));
						p.sendMessage(pl.ChatPrefix + youWon);
						eco.rewardMoney(getPlayer(), output);
						String playerHasWon = pl.PlayerHasWon;
						playerHasWon = playerHasWon.replaceAll("%PlayerName%", getPlayer().getName());
						playerHasWon = playerHasWon.replaceAll("%Reward%", String.valueOf(output));
						Bukkit.broadcastMessage(pl.ChatPrefix + playerHasWon);
					}else{
						String youWon = pl.YouWon;
						youWon = youWon.replaceAll("%Reward%", String.valueOf(getReward()));
						p.sendMessage(pl.ChatPrefix + youWon);
						eco.rewardMoney(getPlayer(), getReward());
						String playerHasWon = pl.PlayerHasWon;
						playerHasWon = playerHasWon.replaceAll("%PlayerName%", getPlayer().getName());
						playerHasWon = playerHasWon.replaceAll("%Reward%", String.valueOf(getReward()));
						Bukkit.broadcastMessage(pl.ChatPrefix + playerHasWon);
					}
					getPlayer().playSound(getPlayer().getLocation(), pl.WinSound, 5, 1);
				}else{
					getPlayer().sendMessage(pl.ChatPrefix + pl.YouLost);
					if(pl.jackpotSystem){
						int towardJackpot = (int) (price * pl.jackpotPercent);
						eco.addJackpot(towardJackpot);
					}
				}
				this.active = false;
			}
			this.inv.setItem(44, addItem(new ItemStack(Material.ARROW), "§bSpin Agian", Lists.newArrayList("§7Click to Spin agian for §a$" + getPrice())));
		}, 160);

	}

	private int columCheck(int slot1, int slot2, int slot3) {
		if(slot3 == -1)return winCheck1(this.inv.getItem(slot1), this.inv.getItem(slot2));
		else return winCheck2(this.inv.getItem(slot1), this.inv.getItem(slot2), this.inv.getItem(slot3));
	}

	public void columTask(int speed, int delay, boolean stop, int slot1, int slot2, int slot3, String key){
		Bukkit.getScheduler().runTaskLater(pl, () -> {
			if(this.tasks.containsKey(key))this.tasks.get(key).cancel();
		}, delay);
		if(!stop){
			Bukkit.getScheduler().runTaskLater(pl, () -> {
				if(getActive()){
					this.tasks.put(key, Bukkit.getScheduler().runTaskTimer(pl, () -> {
						if(getActive()){
							this.inv.setItem(slot3, inv.getItem(slot2));
							this.inv.setItem(slot2, inv.getItem(slot1));
							ItemStack item = db.randomItem();
							if(!item.getType().equals(Material.PAPER))item = addItem(item, " ");
							this.inv.setItem(slot1, item);
							if(key == "c3")getPlayer().playSound(getPlayer().getLocation(), pl.SpinningSound, pl.soundVol, 1);
						}
					}, 0, speed));
				}
			}, delay);
		}
	}

	public int winCheck1(ItemStack i1, ItemStack i2)
	{
		if((i1.getType() == i2.getType()) || ((i2.getType() == Material.PAPER) || (i1.getType() == Material.PAPER))){
			return 2;
		}else{
			return 0;
		}

	}

	public int winCheck2(ItemStack i1, ItemStack i2, ItemStack i3){
		if(i1.getType() == Material.PAPER){
			if(i2.getType() == Material.PAPER){
				if(i3.getType() == Material.PAPER){
					return 3;
				}else{
					return 3;
				}
			}else if(i2.getType() == i3.getType()){
				return 3;
			}else if(i3.getType() == Material.PAPER){
				return 3;
			}else{
				return 2;
			}
		}else if(i2.getType() == Material.PAPER){
			if(i1.getType() == i3.getType()){
				return 3;
			}else{
				return 2;
			}
		}else if(i3.getType() == Material.PAPER){
			if(i1.getType() == i2.getType()){
				return 3;
			}else{
				return 2;
			}
		}else if(i2.getType() == i3.getType()){
			return 3;
		}else{
			return 2;
		}
	}

	public int winningCal(ItemStack item)
	{
		for(ItemOption opt : db.options){
			if(opt.getMaterial() == item.getType()){
				int a = (int) (this.price * 0.5);
				int b = (opt.getReward() * this.result);
				return (int) (b * a);
			}
		}
		if(item.getType() == Material.PAPER){
			int a = (int) (this.price * 0.5);
			int b = (pl.wildReward * this.result);
			return (int) (b * a);
		}else return 0;
	}

	public ItemStack addItem(ItemStack item, String name, List<String> lore) {
		ItemMeta meta;
		meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public ItemStack addItem(ItemStack item, String name) {
		ItemMeta meta;
		meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}
}
