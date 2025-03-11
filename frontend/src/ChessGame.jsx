import React, { useState } from "react";
import { Chessboard } from "react-chessboard";

const ChessBoard = () => {
    const [position, setPosition] = useState("start");

    const onDrop = (sourceSquare, targetSquare) => {
        console.log(`Move: ${sourceSquare} to ${targetSquare}`);
        // TODO: Validate move with backend before updating position
        setPosition((prev) => ({
            ...prev,
            [sourceSquare]: "",
            [targetSquare]: prev[sourceSquare],
        }));
    };

    return (
        <div style={{ width: "500px", margin: "auto" }}>
            <Chessboard position={position} onPieceDrop={onDrop} />
        </div>
    );
};

export default ChessBoard;
