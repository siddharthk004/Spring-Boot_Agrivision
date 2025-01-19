package com.agri.vision.Service;

import org.springframework.stereotype.Service;

import com.agri.vision.Repo.CartRepo;
import com.agri.vision.Repo.wishlistRepo;

@Service
public class UsageService {

    private final wishlistRepo wishlistrepo;
    private final CartRepo cartrepo;

    public UsageService(wishlistRepo wishlistrepo,CartRepo cartrepo) {
        this.wishlistrepo = wishlistrepo;
        this.cartrepo = cartrepo;
    }

    public int countByEmail(String email) {
        return wishlistrepo.countByEmail(email);
    }
    public int countByCartEmail(String email) {
        return cartrepo.countByEmail(email);
    }
}
