package com.scanCrunch.domain.wishlist.dto;

public class WishlistStatusResponse {

    private boolean success;
    private boolean wishlisted;

    public WishlistStatusResponse() {
    }

    public WishlistStatusResponse(boolean success, boolean wishlisted) {
        this.success = success;
        this.wishlisted = wishlisted;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isWishlisted() {
        return wishlisted;
    }

    public void setWishlisted(boolean wishlisted) {
        this.wishlisted = wishlisted;
    }
}
