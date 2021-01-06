package fr.fonkio;


import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

public class Joueur implements Comparable {
    private Member member;
    private boolean isDead;
    private boolean isDisconnected;
    private int numero;

    public Joueur(Member member) {
        this.member = member;
        this.isDead = false;
        this.numero = -1;
    }

    public Member getMember() {
        return member;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public boolean isDisconnected() {
        return isDisconnected;
    }

    public int getNumero() {
        return numero;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setDisconnected(boolean disconnected) {
        isDisconnected = disconnected;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    @Override
    public String toString() {
        return "Joueur{" +
                "member=" + member +
                ", isDead=" + isDead +
                ", isDisconnected=" + isDisconnected +
                ", numero=" + numero +
                '}';
    }


    @Override
    public int compareTo(@NotNull Object o) {
        if (o instanceof Joueur) {
            return ((Integer)this.getNumero()).compareTo(((Joueur)o).getNumero());
        } else {
            return -1;
        }

    }
}
