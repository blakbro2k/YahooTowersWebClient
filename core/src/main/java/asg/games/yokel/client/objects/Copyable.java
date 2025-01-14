package asg.games.yokel.client.objects;

public interface Copyable<T> {
    T copy();

    T deepCopy();
}