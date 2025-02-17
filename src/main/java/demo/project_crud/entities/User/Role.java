package demo.project_crud.entities.User;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import static demo.project_crud.entities.User.Permission.*;

@RequiredArgsConstructor
public enum Role {
    ADMIN(Set.of(
            ADMIN_READ,
            ADMIN_CREATE,
            ADMIN_DELETE,
            ADMIN_UPDATE,
            MANAGER_CREATE,
            MANAGER_UPDATE,
            MANAGER_DELETE,
            MANAGER_READ
    )),
    MANAGER(Set.of(
            MANAGER_CREATE,
            MANAGER_UPDATE,
            MANAGER_DELETE,
            MANAGER_READ
    )),
    USER(Collections.emptySet());

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthrities() {

        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
