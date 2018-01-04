package me.abandoncaptian.TokenSlots;

import java.util.ArrayList;

import org.bukkit.Material;

public class ItemOption {
	
	private Material material;
	private int chance;
	private int reward;
	
	public ItemOption(Material material, int chance, int reward){
		this.material = material;
		this.chance = chance;
		this.reward = reward;
	}
	
	public Material getMaterial(){
		return this.material;
	}
	
	public int getChance(){
		return this.chance;
	}
	
	public int getReward(){
		return this.reward;
	}
	
	public ArrayList<Material> getMaterialChances(){
		ArrayList<Material> output = new ArrayList<Material>();
		for(int i = 1; i <= this.chance; i++){
			output.add(this.material);
		}
		return output;
	}
}
