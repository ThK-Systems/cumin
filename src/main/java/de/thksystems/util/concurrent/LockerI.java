/*
 *
 *  * tksCommons
 *  *
 *  * Author  : Thomas Kuhlmann (ThK-Systems, https://www.thk-systems.de)
 *  * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 *
 */

package de.thksystems.util.concurrent;

public interface LockerI<T> {

    boolean tryLock(T element);

    void lock(T element);

    void unlock(T element);

    boolean isLocked(T element);
}
