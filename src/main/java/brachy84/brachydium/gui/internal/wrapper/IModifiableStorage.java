package brachy84.brachydium.gui.internal.wrapper;

import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;

public interface IModifiableStorage<T> extends SingleSlotStorage<T> {

    /**
     * Overwrites the resource stored in this slot
     * Intended to be used in GUI's
     * @param resource resource to set
     * @param amount amount to set
     * @return true if action was successful
     */
    boolean setResource(T resource, long amount);

    /**
     * @param resource resource to check
     * @return if resource can be inserted
     */
    default boolean canInsert(T resource) {
        return true;
    }
}
