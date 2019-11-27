package outskirts.nbt;

//Storable is liking "Sortable", and less the 'Save' feeling
public interface Savable {

    void readNBT(NBTTagCompound tagCompound);

    NBTTagCompound writeNBT(NBTTagCompound tagCompound);

}
