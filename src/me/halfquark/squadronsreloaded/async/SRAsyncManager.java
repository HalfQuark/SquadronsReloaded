package me.halfquark.squadronsreloaded.async;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import me.halfquark.squadronsreloaded.SquadronsReloaded;
import me.halfquark.squadronsreloaded.formation.FormationFormer;
import me.halfquark.squadronsreloaded.move.CraftRotateManager;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronCraft;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.CruiseDirection;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;

public class SRAsyncManager extends BukkitRunnable {
	
	private static SRAsyncManager inst;
	private final Map<Craft, Integer> cooldownCache = new WeakHashMap<>();
	
	public static void initialize(SquadronsReloaded pl) {
		inst = new SRAsyncManager();
		inst.runTaskTimer(pl, 0, 1);
	}
	
	@Override
	public void run() {
		processCruise();
		updateSquadrons();
	}
	
	@SuppressWarnings("deprecation")
	private void processCruise() {
		for(Squadron sq : SquadronManager.getInstance().getSquadronList()) {
			if (sq == null || !sq.getCruising()) {
                continue;
            }
			// check direct controls to modify movement
            boolean bankLeft = false;
            boolean bankRight = false;
            boolean dive = false;
            if (sq.getPilotLocked()) {
                if (sq.getPilot().isSneaking())
                    dive = true;
                if (sq.getPilot().getInventory().getHeldItemSlot() == 3)
                    bankLeft = true;
                if (sq.getPilot().getInventory().getHeldItemSlot() == 5)
                    bankRight = true;
            }
	        for (SquadronCraft pcraft : sq.getCrafts()) {
	            if (pcraft == null || !pcraft.isNotProcessing()) {
	                continue;
	            }
	            long ticksElapsed = (System.currentTimeMillis() - pcraft.getLastCruiseUpdate()) / 50;
	            World w = pcraft.getWorld();
	            // if the craft should go slower underwater, make
	            // time pass more slowly there
	            if (pcraft.getType().getHalfSpeedUnderwater() && pcraft.getHitBox().getMinY() < w.getSeaLevel())
	                ticksElapsed >>= 1;
	            int tickCoolDown;
	            if(cooldownCache.containsKey(pcraft)){
	                tickCoolDown = cooldownCache.get(pcraft);
	            } else {
	                tickCoolDown = pcraft.getTickCooldown();
	                cooldownCache.put(pcraft,tickCoolDown);
	            }
	            // Account for banking and diving in speed calculations by changing the tickCoolDown
	            if(sq.getCruiseDirection() != CruiseDirection.UP && sq.getCruiseDirection() != CruiseDirection.DOWN) {
	                if (bankLeft || bankRight) {
	                    if (!dive) {
	                        tickCoolDown *= (Math.sqrt(Math.pow(1 + pcraft.getType().getCruiseSkipBlocks(w), 2) + Math.pow(pcraft.getType().getCruiseSkipBlocks(w) >> 1, 2)) / (1 + pcraft.getType().getCruiseSkipBlocks(w)));
	                    } else {
	                        tickCoolDown *= (Math.sqrt(Math.pow(1 + pcraft.getType().getCruiseSkipBlocks(w), 2) + Math.pow(pcraft.getType().getCruiseSkipBlocks(w) >> 1, 2) + 1) / (1 + pcraft.getType().getCruiseSkipBlocks(w)));
	                    }
	                } else if (dive) {
	                    tickCoolDown *= (Math.sqrt(Math.pow(1 + pcraft.getType().getCruiseSkipBlocks(w), 2) + 1) / (1 + pcraft.getType().getCruiseSkipBlocks(w)));
	                }
	            }
	            if (Math.abs(ticksElapsed) < tickCoolDown) {
	                continue;
	            }
	            cooldownCache.remove(pcraft);
	            int dx = 0;
	            int dz = 0;
	            int dy = 0;
	            // ascend
	            if (sq.getCruiseDirection() == CruiseDirection.UP) {
	                dy = 1 + pcraft.getType().getVertCruiseSkipBlocks();
	            }
	            // descend
	            if (sq.getCruiseDirection() == CruiseDirection.DOWN) {
	                dy = -1 - pcraft.getType().getVertCruiseSkipBlocks();
	                if (pcraft.getHitBox().getMinY() <= w.getSeaLevel()) {
	                    dy = -1;
	                }
	            } else if (dive) {
	                dy = -((pcraft.getType().getCruiseSkipBlocks(w) + 1) >> 1);
	                if (pcraft.getHitBox().getMinY() <= w.getSeaLevel()) {
	                    dy = -1;
	                }
	            }
	            // ship faces west
	            if (sq.getCruiseDirection() == CruiseDirection.WEST) {
	                dx = -1 - pcraft.getType().getCruiseSkipBlocks(w);
	                if (bankRight) {
	                    dz = (-1 - pcraft.getType().getCruiseSkipBlocks(w)) >> 1;
	                }
	                if (bankLeft) {
	                    dz = (1 + pcraft.getType().getCruiseSkipBlocks(w)) >> 1;
	                }
	            }
	            // ship faces east
	            if (sq.getCruiseDirection() == CruiseDirection.EAST) {
	                dx = 1 + pcraft.getType().getCruiseSkipBlocks(w);
	                if (bankLeft) {
	                    dz = (-1 - pcraft.getType().getCruiseSkipBlocks(w)) >> 1;
	                }
	                if (bankRight) {
	                    dz = (1 + pcraft.getType().getCruiseSkipBlocks(w)) >> 1;
	                }
	            }
	            // ship faces north
	            if (sq.getCruiseDirection() == CruiseDirection.SOUTH) {
	                dz = 1 + pcraft.getType().getCruiseSkipBlocks(w);
	                if (bankRight) {
	                    dx = (-1 - pcraft.getType().getCruiseSkipBlocks(w)) >> 1;
	                }
	                if (bankLeft) {
	                    dx = (1 + pcraft.getType().getCruiseSkipBlocks(w)) >> 1;
	                }
	            }
	            // ship faces south
	            if (sq.getCruiseDirection() == CruiseDirection.NORTH) {
	                dz = -1 - pcraft.getType().getCruiseSkipBlocks(w);
	                if (bankLeft) {
	                    dx = (-1 - pcraft.getType().getCruiseSkipBlocks(w)) >> 1;
	                }
	                if (bankRight) {
	                    dx = (1 + pcraft.getType().getCruiseSkipBlocks(w)) >> 1;
	                }
	            }
	            if (pcraft.getType().getCruiseOnPilot()) {
	                dy = pcraft.getType().getCruiseOnPilotVertMove();
	            }
	            if (pcraft.getType().getGearShiftsAffectCruiseSkipBlocks()) {
	                final int gearshift = pcraft.getCurrentGear();
	                dx *= gearshift;
	                dy *= gearshift;
	                dz *= gearshift;
	            }
	            pcraft.translate(dx, dy, dz);
	            pcraft.setLastTranslation(new MovecraftLocation(dx, dy, dz));
	            if (pcraft.getLastCruiseUpdate() != -1) {
	                pcraft.setLastCruiseUpdate(System.currentTimeMillis());
	            } else {
	                pcraft.setLastCruiseUpdate(System.currentTimeMillis() - 30000);
	            }
	        }
		}
    }
	
	private void updateSquadrons() {
		for(Squadron sq : SquadronManager.getInstance().getSquadronList()) {
			if(sq == null) {
				SquadronManager.getInstance().removeSquadron(sq);
			}
			if(sq.getCrafts() == null) {
				SquadronManager.getInstance().removeSquadron(sq);
			}
			for(SquadronCraft craft : sq.getCrafts()) {
				CraftRotateManager.getInstance().adjustDirection(sq, craft);
				if(sq.getCruising())
					continue;
				if(sq.isFormingUp())
					FormationFormer.formUp(craft);
				craft.setLastCruiseUpdate(System.currentTimeMillis());
			}
		}
	}


}
