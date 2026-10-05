package com.alex.irminsul.entity;

import java.io.Serializable;
import java.util.Objects;

public class UserGameId implements Serializable {

    private User user;
    private Game game;

    public UserGameId() {
    }

    public UserGameId(User user, Game game) {
        this.user = user;
        this.game = game;
    }

    // getters y setters

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserGameId)) return false;

        UserGameId that = (UserGameId) o;

        return Objects.equals(user, that.user)
                && Objects.equals(game, that.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, game);
    }
}