package de.guntram.mcmod.durabilityviewer.client.gui;

public enum Corner
{
    BOTTOM_RIGHT, 
    BOTTOM_LEFT, 
    TOP_RIGHT, 
    TOP_LEFT;
    
    public boolean isLeft() {return this==TOP_LEFT || this==BOTTOM_LEFT;}
    public boolean isRight() {return this==TOP_RIGHT || this==BOTTOM_RIGHT;}
    public boolean isTop() { return this==TOP_LEFT || this==TOP_RIGHT; }
    public boolean isBottom() { return this==BOTTOM_LEFT || this==BOTTOM_RIGHT; }
}
