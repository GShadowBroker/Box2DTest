package com.gledyson.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class GunComponent implements Component, Pool.Poolable {
    private static final String TAG = GunComponent.class.getSimpleName();

    public int maxAmmo = 6;
    public int maxSpareBullets = 99;
    public float fireRate = 0.5f;

    public int spareBullets = maxAmmo;
    public int ammo = maxAmmo;

    // true if reloaded, false if nothing changed

    /**
     * reloads the gun if there are spare bullets
     *
     * @return true if reloaded, false if nothing changed
     */
    public boolean reload() {
        int bulletsToReload = maxAmmo - ammo;

        if (bulletsToReload <= 0) return false;

        // if there are not enough bullets left, reload what there is left
        if (spareBullets - bulletsToReload <= 0) bulletsToReload = spareBullets;

        if (bulletsToReload > 0) {
            spareBullets -= bulletsToReload;
            ammo = maxAmmo;
            return true;
        }
        return false;
    }

    /**
     * adds bullets to inventory
     */
    public void addBullets(int quantity) {
        if (spareBullets + quantity > 99) {
            spareBullets = 99;
        } else {
            spareBullets += quantity;
        }
    }

    @Override
    public void reset() {
        maxAmmo = 6;
        maxSpareBullets = 99;
        fireRate = 0.5f;
        spareBullets = maxAmmo;
        ammo = maxAmmo;
    }
}
