package io.github.manoelpiovesan.utils;

import io.github.manoelpiovesan.entities.User;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
public class Startup {

    @Transactional
    public void loadUsers(@Observes StartupEvent event) {
        if (User.count() > 0) {
            User.deleteAll();
        }
        User.add("user", "user", "user");
        User.add("admin", "admin", "admin");
        User.add("manoel", "manoel", "user,admin");
    }

}
