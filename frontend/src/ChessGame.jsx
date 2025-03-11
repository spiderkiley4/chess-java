import React, { useState } from "react";
import { Chessboard } from "react-chessboard";

const ChessBoard = () => {
    const [position, setPosition] = useState("start");

    return (
        <Chessboard
            position={position}
            onPieceDrop={(sourceSquare, targetSquare) => {
                console.log(`Move from ${sourceSquare} to ${targetSquare}`);
                setPosition((prev) => ({ ...prev, [sourceSquare]: "", [targetSquare]: "p" })); // Example move
            }}
        />
    );
};

export default ChessBoard;
