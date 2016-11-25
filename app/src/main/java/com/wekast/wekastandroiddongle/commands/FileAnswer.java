package com.wekast.wekastandroiddongle.commands;

import static com.wekast.wekastandroiddongle.Utils.Utils.DONGLE_SOCKET_PORT_FILE_TRANSFER;

public class FileAnswer extends Answer {

    public FileAnswer() {
        setType("file");
        add("message", "ok");
        add("port", DONGLE_SOCKET_PORT_FILE_TRANSFER);
    }

}
