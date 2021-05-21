package io.takamaka.ERC721;

import io.takamaka.code.lang.Contract;
import io.takamaka.code.math.UnsignedBigInteger;

public interface IERC721Receiver {
    public byte onERC721Received(Contract operator, Contract from, UnsignedBigInteger tokenId, byte Data);
}
