package io.takamaka.ERC721;

import io.takamaka.code.lang.Contract;
import io.takamaka.code.lang.View;
import io.takamaka.code.math.UnsignedBigInteger;

public interface IERC721View {
    @View UnsignedBigInteger balanceOf(Contract owner);
    @View Contract ownerOf(UnsignedBigInteger tokenId);
    IERC721View snapshot();
}
