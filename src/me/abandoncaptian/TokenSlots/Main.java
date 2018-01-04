package me.abandoncaptian.TokenSlots;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Charsets;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements Listener{

	Logger Log = Bukkit.getLogger();
	public Economy econ = null;
	File configFile;
	FileConfiguration config;
	File langFile;
	FileConfiguration langConfig;
	public EcoMan em;
	SignMan sm;
	DataBase db;
	int maxBet = 0;
	double winTax = 0;
	boolean wild = true;
	int wildReward = 0;
	boolean spinSound = true;
	int soundVol = 1;
	double jackpotPercent = 0;
	boolean jackpotSystem = true;
	ItemStack filler;
	HashMap<String, Inventory> machines = new HashMap<String, Inventory>();
	public Sound SpinningSound;
	public Sound WinSound;
	public Sound LoseSound;
	//General
	public String ChatPrefix;
	//Commands
	public String InvalidArguments;
	public String TooManyArguments;
	public String SlotsArgumentUsage;
	public String NeededForMax;
	public String NotNumberOrMax;
	public String WhatWill0Do;
	public String BetTooHigh;
	public String NotEnoughMoney;
	//Machine
	public String CooldownActive;
	public String PlayerHasWon;
	public String YouLost;
	public String CooldownFinished;
	public String ClosedGUITooEarly;
	public String Connected;
	//Econ
	public String YouWon;
	public String YouWonWithJackpot;
	private static Main instance;

	@Override
	public void onEnable()
	{
		if (!setupEconomy() ) {
			Log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		Log.info("---------- [ Token Slots v4.2 ] ----------");
		Log.info(" ");
		Log.info("                  Enabled!              ");
		Log.info(" ");
		Log.info("------------------------------------------");
		instance = this;
		loadFiles();
		reload();
		em = new EcoMan(this);
		sm = new SignMan(this);
		db = new DataBase(this);
		loadJackpot();
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(sm, this);
		Bukkit.getPluginManager().registerEvents(db, this);
	}

	@Override
	public void onDisable()
	{
		Log.info("---------- [ Token Slots v4.2 ] ----------");
		Log.info(" ");
		Log.info("                  Disabled!              ");
		Log.info(" ");
		Log.info("------------------------------------------");
		em.saveJackpot();
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public static Main getMain(){
		return instance;
	}


	@EventHandler
	public void invManagment(InventoryClickEvent e)
	{
		if(e.getInventory().getName().startsWith("§7§l[§b§lSlots§7§l]")){
			e.setCancelled(true);
		}

	}
	public void loadFiles(){
		this.configFile = new File(this.getDataFolder().toString() + File.separatorChar + "config.yml");
		this.langFile = new File(this.getDataFolder().toString() + File.separatorChar + "language.yml");
		if (!configFile.exists()) {
			this.saveResource("config.yml", false);
		}
		if (!langFile.exists()) {
			this.saveResource("language.yml", false);
		}

		// reload configuration
		try {
			this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(configFile), Charsets.UTF_8));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			this.langConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(langFile), Charsets.UTF_8));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void reload(){
		maxBet = 0;
		winTax = 0;
		wild = true;
		wildReward = 0;
		spinSound = true;
		soundVol = 1;
		String tempVal;
		String[] tempValSplit;
		maxBet = config.getInt("MaxBet");
		if((maxBet < 1))return;
		winTax = config.getDouble("WinTax");
		wild = config.getBoolean("WildCard");
		wildReward = config.getInt("WildCardReward");
		spinSound = config.getBoolean("SpinSound");
		soundVol = config.getInt("SoundVolume");
		loadLanguage();

		tempVal = config.getString("SpaceFiller");
		tempValSplit = tempVal.split("/");
		filler = new ItemStack(Material.matchMaterial(tempValSplit[0]), 1, (short) Short.valueOf(tempValSplit[1]));
	}

	public void loadLanguage(){
		String s1 = config.getString("SpinningSound");
		String s2 = config.getString("LoseSound");
		String s3 = config.getString("WinSound");
		SpinningSound = Sound.valueOf(s1);
		LoseSound = Sound.valueOf(s2);
		WinSound = Sound.valueOf(s3);
		ChatPrefix = getStringFromLang("ChatPrefix");
		InvalidArguments = getStringFromLang("InvalidArguments");
		TooManyArguments = getStringFromLang("TooManyArguments");
		SlotsArgumentUsage = getStringFromLang("SlotsArgumentUsage");
		NeededForMax = getStringFromLang("NeededForMax");
		NotNumberOrMax = getStringFromLang("NotNumberOrMax");
		WhatWill0Do = getStringFromLang("WhatWill0Do");
		NotEnoughMoney = getStringFromLang("NotEnoughMoney");
		BetTooHigh = getStringFromLang("BetTooHigh");
		CooldownActive = getStringFromLang("CooldownActive");
		PlayerHasWon = getStringFromLang("PlayerHasWon");
		YouLost = getStringFromLang("YouLost");
		CooldownFinished = getStringFromLang("CooldownFinished");
		ClosedGUITooEarly = getStringFromLang("ClosedGUITooEarly");
		Connected = getStringFromLang("Connected");
		YouWon = getStringFromLang("YouWon");
		YouWonWithJackpot = getStringFromLang("YouWonWithJackpot");
		Log.info("Loaded Language File and Custom Sounds");
	}
	public void loadJackpot(){
		if(config.contains("JackpotSystem")){
			jackpotSystem = config.getBoolean("JackpotSystem");
			if(jackpotSystem){
				jackpotPercent = config.getDouble("JackpotPercent");
				em.addJackpot(config.getInt("DontTouch"));
			}
		}else{
			jackpotSystem = true;
			config.set("JackpotSystem", true);
			config.set("JackpotPercent", 0.5);
			config.set("DontTouch", 0);
			em.addJackpot(0);
			jackpotPercent = 0.5;
			try {
				config.save(configFile);
			} catch (IOException e) {}
		}
	}

	public String getStringFromLang(String string){
		String text = langConfig.getString(string);
		if(text == null) return null;
		if(text.contains("&")){
			text = text.replaceAll("&", "§");
		}
		return text;
	}

	public boolean onCommand(CommandSender theSender, Command cmd, String commandLabel, String[] args)
	{
		if (commandLabel.equalsIgnoreCase("tokenslots"))
		{
			if(args.length == 0){
				theSender.sendMessage(InvalidArguments);
				theSender.sendMessage("§9Usage: §b/tokenslots reload or settings");
				return true;
			}else if(args[0].equalsIgnoreCase("reload") && (theSender.hasPermission("tokenslots.reload") || theSender.hasPermission("tokenslots.*")))
			{
				if(args.length == 1){
					Bukkit.getScheduler().runTaskLater(this,  new Runnable(){
						@Override
						public void run() {
							reload();
							theSender.sendMessage(ChatPrefix +" §aReload Successful!");
						}
					}, 10);
					return true;
				}else if(args.length > 1){
					theSender.sendMessage("§cToo Many Arguments!");
					theSender.sendMessage("§9Usage: §b/tokenslots reload");
					return true;
				}
			}else if(args[0].equalsIgnoreCase("settings") && (theSender.hasPermission("tokenslots.settings") || theSender.hasPermission("tokenslots.*")))
			{
				if(args.length == 3){
					if(args[1].equalsIgnoreCase("maxbet")){
						try{
							Integer.valueOf(args[2]);
						} catch (NumberFormatException ex){
							theSender.sendMessage(ChatPrefix + "§cThat is not a number!" );
							return true;
						}
						int val =  Integer.valueOf(args[2]);
						config.set("MaxBet", val);
						try {
							config.save(configFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
						maxBet = val;
						theSender.sendMessage(ChatPrefix + "§6MaxBet has been set to " + val);
						return true;
					}
					else if(args[1].equalsIgnoreCase("wintax")){
						try{
							Double.valueOf(args[2]);
						} catch (NumberFormatException ex){
							theSender.sendMessage(ChatPrefix + "§cThat is not a decimal!" );
							return true;
						}
						double val =  Double.valueOf(args[2]);
						if(val >= 1){
							theSender.sendMessage(ChatPrefix + "§cThat is not a decimal!" );
						}
						config.set("WinTax", val);
						try {
							config.save(configFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
						winTax = val;
						theSender.sendMessage(ChatPrefix + "§6WinTax has been set to " + val);
						return true;
					}
					else if(args[1].equalsIgnoreCase("wildcard")){
						try{
							Boolean.valueOf(args[2].toLowerCase());
						} catch (NumberFormatException ex){
							theSender.sendMessage(ChatPrefix + "§cThat is not true or false!" );
							return true;
						}
						boolean val =  Boolean.valueOf(args[2].toLowerCase());
						config.set("WildCard", val);
						try {
							config.save(configFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
						if(val){
							if(!db.choices.contains(Material.PAPER)){
								db.choiceSize++;
								db.choices.add(Material.PAPER);
							}
						}else{
							if(db.choices.contains(Material.PAPER)){
								db.choiceSize--;
								db.choices.remove(Material.PAPER);
							}
						}
						wild = val;
						theSender.sendMessage(ChatPrefix + "§6WildCard has been set to " + val);
						return true;
					}
					else if(args[1].equalsIgnoreCase("spinsound")){
						try{
							Boolean.valueOf(args[2].toLowerCase());
						} catch (NumberFormatException ex){
							theSender.sendMessage(ChatPrefix + "§cThat is not true or false!" );
							return true;
						}
						boolean val =  Boolean.valueOf(args[2].toLowerCase());
						config.set("SpinSound", val);
						try {
							config.save(configFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
						spinSound = val;
						theSender.sendMessage(ChatPrefix + "§6SpinSound has been set to " + val);
						return true;
					}
					else if(args[1].equalsIgnoreCase("soundvolume") || args[1].equalsIgnoreCase("soundvolume")){
						try{
							Integer.valueOf(args[2]);
						} catch (NumberFormatException ex){
							theSender.sendMessage(ChatPrefix + "§cThat is not a number!" );
							return true;
						}
						int val =  Integer.valueOf(args[2]);
						config.set("SoundVolume", val);
						try {
							config.save(configFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
						soundVol = val;
						theSender.sendMessage(ChatPrefix + "§6Sound Volume has been set to " + val);
						return true;
					}
					else{
						theSender.sendMessage("§9Settings:");
						theSender.sendMessage("§b/tokenslots settings MaxBet <NUMBER>");
						theSender.sendMessage("§b/tokenslots settings WinTax <DECIMAL>");
						theSender.sendMessage("§b/tokenslots settings WildCard <true/false>");
						theSender.sendMessage("§b/tokenslots settings SpinSound <true/false>");
						theSender.sendMessage("§b/tokenslots settings SoundVolume <NUMBER>");
						return true;
					}
				}else if(args.length == 1){
					theSender.sendMessage("§9Usage: §b/tokenslots settings <Setting> <Value>");
					theSender.sendMessage("§9Settings:");
					theSender.sendMessage("§b/tokenslots settings MaxBet <NUMBER>");
					theSender.sendMessage("§b/tokenslots settings WinTax <DECIMAL>");
					theSender.sendMessage("§b/tokenslots settings WildCard <true/false>");
					theSender.sendMessage("§b/tokenslots settings SpinSound <true/false>");
					theSender.sendMessage("§b/tokenslots settings SoundVolume <NUMBER>");
					return true;
				}else if(args.length == 2){
					if(args[1].equalsIgnoreCase("maxbet"))theSender.sendMessage("§b/tokenslots settings MaxBet <NUMBER>");
					else if(args[1].equalsIgnoreCase("wintax"))theSender.sendMessage("§b/tokenslots settings WinTax <DECIMAL>");
					else if(args[1].equalsIgnoreCase("wildcard"))theSender.sendMessage("§b/tokenslots settings WildCard <true/false>");
					else if(args[1].equalsIgnoreCase("spinsound"))theSender.sendMessage("§b/tokenslots settings SpinSound <true/false>");
					else if(args[1].equalsIgnoreCase("soundvolume"))theSender.sendMessage("§b/tokenslots settings SoundVolume <NUMBER>");
					else{
						theSender.sendMessage("§9Settings:");
						theSender.sendMessage("§b/tokenslots settings MaxBet <NUMBER>");
						theSender.sendMessage("§b/tokenslots settings WinTax <DECIMAL>");
						theSender.sendMessage("§b/tokenslots settings WildCard <true/false>");
						theSender.sendMessage("§b/tokenslots settings SpinSound <true/false>");
						theSender.sendMessage("§b/tokenslots settings SoundVolume <NUMBER>");
					}
					return true;
				}else if(args.length > 3){
					theSender.sendMessage("§cToo Many Arguments!");
					theSender.sendMessage("§9Usage: §b/tokenslots settings <Setting> <Value>");
					return true;
				}
			}else{
				theSender.sendMessage(InvalidArguments);
				theSender.sendMessage("§9Usage: §b/tokenslots reload or settings");
				return true;
			}
		}
		if (commandLabel.equalsIgnoreCase("slots") && (theSender instanceof Player))
		{
			Player p = (Player)theSender;
			if(args.length == 1){
				if(args[0].equalsIgnoreCase("max")){
					if(em.MoneyCheck(p, maxBet)){
						db.runMachine(p, maxBet);
						em.removeMoney(p, maxBet);
						return true;
					}else{
						p.sendMessage(ChatPrefix + NeededForMax.replace("%MaxBet%", String.valueOf(maxBet)));
						return true;
					}
				}
				try{
					Integer.valueOf(args[0]);
				} catch (NumberFormatException ex){
					p.sendMessage(ChatPrefix + NotNumberOrMax );
					return true;
				}
				if(Integer.parseInt(args[0]) <= maxBet){
					if(em.MoneyCheck(p, Integer.parseInt(args[0]))){
						if(Integer.parseInt(args[0]) >= 1){



							db.runMachine(p, Integer.parseInt(args[0]));
							em.removeMoney(p, Integer.parseInt(args[0]));
							return true;
						}else{
							p.sendMessage(ChatPrefix + WhatWill0Do);
							return true;
						}
					}else{
						p.sendMessage(ChatPrefix + NotEnoughMoney);
						return true;
					}
				}else
				{
					p.sendMessage(ChatPrefix + BetTooHigh.replace("%MaxBet%", String.valueOf(maxBet)));
					return true;
				}

			}else if (args.length > 1)
			{
				p.sendMessage(TooManyArguments);
				p.sendMessage("§7--------" + ChatPrefix + "§7--------");
				p.sendMessage(" ");
				p.sendMessage(SlotsArgumentUsage);
				p.sendMessage(" ");
				return true;
			}else
			{
				p.sendMessage("§7--------" + ChatPrefix + "§7--------");
				p.sendMessage(" ");
				p.sendMessage(SlotsArgumentUsage);
				p.sendMessage(" ");
				return true;
			}
		}
		return true;
	}
}