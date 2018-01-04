package me.abandoncaptian.TokenSlots;

import java.io.IOException;

import org.bukkit.entity.Player;

public class EcoMan{
	Main pl;
	int winning = 0;
	private static EcoMan instance;
	private int Jackpot;
	public EcoMan(Main plugin){
		this.pl = plugin;
		instance = this;
		this.Jackpot = 0;
	}

	@SuppressWarnings("deprecation")
	public void rewardMoney(Player p, double amount){
		pl.econ.depositPlayer(p.getName(), amount);
		return;
	}

	@SuppressWarnings("deprecation")
	public void removeMoney(Player p, int amount){
		pl.econ.withdrawPlayer(p.getName(), amount);
	}

	@SuppressWarnings("deprecation")
	public boolean MoneyCheck(Player p, int amount){	
		if(pl.econ.getBalance(p.getName()) >= amount)return true;
		else return false;
	}

	public static EcoMan getEM(){
		return instance;
	}
	
	public void addJackpot(int money){
		this.Jackpot = this.Jackpot + money;
	}
	
	public int getJackpot(){
		return this.Jackpot;
	}
	
	public void resetJackpot(){
		this.Jackpot = 0;
	}
	
	
	public void saveJackpot(){
		pl.config.set("DontTouch", this.Jackpot);
		try {
			pl.config.save(pl.configFile);
		} catch (IOException e){}
	}
}
