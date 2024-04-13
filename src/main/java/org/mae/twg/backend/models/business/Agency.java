package org.mae.twg.backend.models.business;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.auth.User;

import java.util.ArrayList;
import java.util.List;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "agencies")
public class Agency implements Model {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "agency_id")
    private Long id;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted = Boolean.FALSE;

    private Double latitude;
    private Double longitude;

    @OneToMany(mappedBy = "agency",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    private List<AgencyLocal> locals = new ArrayList<>();

    @OneToMany(mappedBy = "agency",
            cascade = CascadeType.DETACH,
            fetch = FetchType.EAGER)
    private List<User> agents = new ArrayList<>();

    public void addLocal(AgencyLocal local) {
        locals.add(local);
        local.setAgency(this);
    }

    public void removeLocal(AgencyLocal local) {
        locals.remove(local);
        local.setAgency(null);
    }

    public void addAgent(User user) {
        agents.add(user);
        user.setAgency(this);
    }

    public void removeAgent(User user) {
        agents.remove(user);
        user.setAgency(null);
    }

    @Override
    public List<AgencyLocal> getLocalizations() {
        return locals;
    }
}
