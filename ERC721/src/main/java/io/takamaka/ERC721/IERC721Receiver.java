package io.takamaka.ERC721;

import io.takamaka.code.lang.Contract;

import java.math.BigInteger;

public interface IERC721Receiver {
    public static byte onERC721Received(Contract operator, Contract from, BigInteger tokenId, byte Data) {
        return 0;
    }
}
