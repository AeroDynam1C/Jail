package com.graywolf336.jail.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.graywolf336.jail.beans.Cell;
import com.graywolf336.jail.beans.Jail;
import com.graywolf336.jail.beans.Prisoner;

/**
 * Event thrown after a prisoner is released.
 * 
 * <p>
 * 
 * This event is called after everything for the releasing takes place.
 * This event is called for informative purposes, see {@link PrePrisonerReleasedEvent}
 * for the event called before they teleported out and all that fancy stuff.
 * 
 * @author graywolf336
 * @since 3.0.0
 * @version 1.0.0
 */
public class PrisonerReleasedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Jail jail;
    private Cell cell;
    private Prisoner prisoner;
    private Player player;

    /**
     * Creates a new {@link PrisonerReleasedEvent prisoner released event} for the given player.
     * 
     * @param jail The jail the prisoner will be jailed at.
     * @param cell The cell we're going to be sending the prisoner to, can be null.
     * @param prisoner The prisoner data.
     * @param player The player being jailed.
     */
    public PrisonerReleasedEvent(Jail jail, Cell cell, Prisoner prisoner, Player player) {
        this.jail = jail;
        this.cell = cell;
        this.prisoner = prisoner;
        this.player = player;
    }

    /** Gets the {@link Jail} this prisoner is coming from. */
    public Jail getJail() {
        return this.jail;
    }

    /** Gets the cell where the prisoner was jailed in, null if they weren't in one. */
    public Cell getCell() {
        return this.cell;
    }
    
    /** Checks if there was a cell involved. */
    public boolean hasCell() {
    	return this.cell != null;
    }

    /** Gets the {@link Prisoner} data for this prisoner. */
    public Prisoner getPrisoner() {
        return this.prisoner;
    }

    /** Gets the instance of the player being released. */
    public Player getPlayer() {
        return this.player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
