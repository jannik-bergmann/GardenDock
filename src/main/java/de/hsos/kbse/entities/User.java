/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

/** User Entity
 *
<<<<<<< HEAD
 * @author Bastian Lührs-Püllmann
=======
 * @author Basti & Jannik Bergmann
>>>>>>> d5b342fc64ced32b8087012d8fe20d3e2a43f3d6
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class User implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @EqualsAndHashCode.Include
    @ToString.Include
    private String userId;

    @ToString.Include
    @NotNull
    private String username;
    @ToString.Include
    @NotNull
    private String pwdhash;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    private Set<Arduino> arduinos = new HashSet<>();
}
