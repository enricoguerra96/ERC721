package io.takamaka.ERC721;

import io.takamaka.code.lang.Contract;
import io.takamaka.code.lang.View;

import java.math.BigInteger;

public interface IERC721View {
    @View BigInteger balanceOf(Contract owner);
    @View Contract ownerOf(BigInteger tokenId);
    IERC721View snapshot();
}
