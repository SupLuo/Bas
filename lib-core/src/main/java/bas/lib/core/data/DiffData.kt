package bas.lib.core.data

interface DiffData<T> {
    /**
     * Called to decide whether two objects represent the same item.
     *
     * @param other The item in the new list.
     * @return True if the two items represent the same object or false if they are different.
     */
    fun areItemsTheSame(other: T): Boolean

    /**
     * Called to decide whether two items have the same data. This information is used to detect if
     * the contents of an item have changed.
     *
     * @param other The item in the new list.
     * @return True if the contents of the items are the same or false if they are different.
     */
    fun areContentsTheSame(other: T): Boolean

    /**
     * Called to get a change payload between an old and new version of an item.
     *
     * @see DiffUtil.Callback#getChangePayload(int, int)
     */
    @SuppressWarnings("WeakerAccess")
    fun getChangePayload(other: T): Any? {
        return null
    }
}