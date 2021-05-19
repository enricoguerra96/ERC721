package io.takamaka.ERC721;

import io.takamaka.code.lang.Contract;
import io.takamaka.code.math.UnsignedBigInteger;

public class ERC721example implements IERC721{
    @Override
    public void safeTransferFrom(Contract from, Contract to, UnsignedBigInteger tokenId, byte data) {

    }

    @Override
    public void safeTransferFrom(Contract from, Contract to, UnsignedBigInteger tokenId) {

    }

    @Override
    public void TransferFrom(Contract from, Contract to, UnsignedBigInteger tokenId) {

    }

    @Override
    public void approve(Contract approved, UnsignedBigInteger tokenId) {

    }

    @Override
    public void setApprovalForAll(Contract operator, boolean approved) {

    }

    @Override
    public Contract getApproved(UnsignedBigInteger tokenId) {
        return null;
    }

    @Override
    public boolean isApprovedForAll(UnsignedBigInteger owner, Contract operator) {
        return false;
    }

    @Override
    public UnsignedBigInteger balanceOf(Contract owner) {
        return null;
    }

    @Override
    public Contract ownerOf(UnsignedBigInteger tokenId) {
        return null;
    }

    @Override
    public IERC721View snapshot() {
        return null;
    }
}
