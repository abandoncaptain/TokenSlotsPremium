package me.abandoncaptian.TokenSlots;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignMan implements Listener{
	Main pl;
	int winning = 0;
	public SignMan(Main plugin){
		this.pl = plugin;
	}

	@EventHandler
	public void onSign(SignChangeEvent e){
		if(e.getLine(0).equalsIgnoreCase("[Slots]") || e.getLine(0).equalsIgnoreCase("[ts]") || e.getLine(0).equalsIgnoreCase("ts") || e.getLine(0).equalsIgnoreCase("slots")){
			if(e.getPlayer().hasPermission("tokenslots.signcreate") || e.getPlayer().hasPermission("tokenslots.*")){
				Player p = e.getPlayer();
				e.setLine(0, "§7§l[§b§lSlots§7§l]");
				String line = e.getLine(2);
				if(line.equalsIgnoreCase("max") || line.equalsIgnoreCase("max bet")){
					e.setLine(2, "§b§lMax Bet");
					p.sendMessage(pl.ChatPrefix + "§6Max Bet sign was created!");
					return;
				}
				try{
					Integer.valueOf(line);
				} catch (NumberFormatException ex){
					p.sendMessage(pl.ChatPrefix + "§cThat is not a number or the word max!" );
					e.getBlock().breakNaturally();
					return;
				}
				int bet = Integer.valueOf(line);
				if(bet <= pl.maxBet){
					e.setLine(2, ("§b§lBet: §a§l"+line));
					p.sendMessage(pl.ChatPrefix + "§6a $" + bet + " Bet sign was created!");
				}else{
					p.sendMessage(pl.ChatPrefix + "§cThat number is higher than the max bet!");
					e.getBlock().breakNaturally();
				}
			}else{
				e.getPlayer().sendMessage(pl.ChatPrefix + "§cYou don't have permission to make a Token Slots sign!");
				e.getBlock().breakNaturally();
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
		if(!(e.getAction() == Action.RIGHT_CLICK_BLOCK))return;
		if(e.getClickedBlock().getState() instanceof Sign){
			Player p = (Player) e.getPlayer();
			Sign s = (Sign) e.getClickedBlock().getState();
			if(s.getLine(0).equals("§7§l[§b§lSlots§7§l]")){
				String line = s.getLine(2);
				if(line.equalsIgnoreCase("§b§lMax Bet")){
					if(pl.em.MoneyCheck(p, pl.maxBet)){
						pl.em.removeMoney(p, pl.maxBet);
						pl.db.runMachine(p, pl.maxBet);
						return;
					}else{
						p.sendMessage("§7§l[§b§lSlots§7§l] §cYou need §a$" + pl.maxBet + " §cto do a max bet");
						return;
					}
				}
				String[] nLine = line.split("§b§lBet: §a§l");
				int bet = Integer.valueOf(nLine[1]);
				try{
					Integer.valueOf(bet);
				} catch (NumberFormatException ex){
					p.sendMessage("§7§l[§b§lSlots§7§l] §c" + ex);
					return;
				}
				if(bet <= pl.maxBet){
					if(pl.em.MoneyCheck(p, bet)){
						pl.em.removeMoney(p, bet);
						pl.db.runMachine(p, bet);
						return;
					}else{
						p.sendMessage("§cInsufficient Funds!");
						return;
					}
				}
			}
		}
	}
}
