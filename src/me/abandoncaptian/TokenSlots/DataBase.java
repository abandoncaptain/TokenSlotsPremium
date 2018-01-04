package me.abandoncaptian.TokenSlots;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DataBase implements Listener{

	Main pl;
	ArrayList<Machine> machines = new ArrayList<Machine>();
	ArrayList<Material> choices = new ArrayList<Material>();
	ArrayList<ItemOption> options = new ArrayList<ItemOption>();
	int choiceSize;
	private static DataBase instance;

	public DataBase(Main plugin){
		pl = plugin;
		instance = this;
		choices.clear();
		choiceSize = 0;
		int conItemsSize = 1;
		Boolean check = true;
		while(check){
			if(pl.config.contains("Items."+conItemsSize+".Item") && pl.config.contains("Items."+conItemsSize+".Chance") && pl.config.contains("Items."+conItemsSize+".Reward")){
				conItemsSize++;
			}else{
				check = false;
			}
		}
		for(int i = 1; i < conItemsSize; i++){
			ItemOption item = new ItemOption(Material.matchMaterial(pl.config.getString("Items."+i+".Item")), pl.config.getInt("Items."+i+".Chance"), pl.config.getInt("Items."+i+".Reward"));
			options.add(item);
		}
		for(ItemOption item : options){
			choices.addAll(item.getMaterialChances());
		}
		choiceSize = choices.size();
		if(pl.wild){
			choiceSize++;
			choices.add(Material.PAPER);
		}
		Bukkit.broadcastMessage("Choices: " + choices);
	}

	public void runMachine(Player p, int price){
		Machine machine = new Machine(p, price);
		machines.add(machine);
		machine.createInventory();
		machine.Run();
	}

	public ItemStack randomItem()
	{
		int numGen = (int)(Math.random() * choiceSize);
		ItemStack item = new ItemStack(choices.get(numGen), 1);
		if(item.getType() == Material.PAPER){
			ItemMeta im = item.getItemMeta();
			im.setDisplayName("§bWild");
			item.setItemMeta(im);
			return item;
		}else{
			return item;
		}
	}

	public static DataBase getDB(){
		return instance;
	}

	@EventHandler
	public void invClose(InventoryCloseEvent e){
		if(e.getInventory() == null)return;
		if(e.getInventory().getName() == null)return;
		if(e.getInventory().getName().equals("§7§l[§b§lSlots§7§l]")){
			for(Machine mach : machines){
				if(mach.getPlayer() == e.getPlayer()){
					if(mach.getActive()){
						mach.Cancel();
						mach.setActive(false);
						mach.getPlayer().sendMessage(pl.ChatPrefix + pl.ClosedGUITooEarly);
						return;
					}else{
						machines.remove(mach);
						break;
					}
				}
			}
		}
	}

	@EventHandler
	public void invClose(InventoryClickEvent e){
		if(e.getInventory() == null)return;
		if(e.getInventory().getName() == null)return;
		if(e.getInventory().getName().equals("§7§l[§b§lSlots§7§l]")){
			for(Machine mach : machines){
				if(mach.getPlayer() == e.getWhoClicked()){
					if(e.getCurrentItem() == null)return;
					if(!e.getCurrentItem().hasItemMeta())return;
					if(!e.getCurrentItem().getItemMeta().hasDisplayName())return;
					if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§bSpin Agian")){
						mach.invSpaceFill();
						mach.Run();
					}
				}
			}
		}
	}
}
