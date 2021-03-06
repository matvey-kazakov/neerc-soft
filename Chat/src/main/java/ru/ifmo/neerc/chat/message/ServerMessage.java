/*
   Copyright 2009 NEERC team

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
// $Id$
/**
 * Date: 25.10.2004
 */
package ru.ifmo.neerc.chat.message;

/**
 * @author Matvey Kazakov
 */
public class ServerMessage extends Message {
    private String text;

    public ServerMessage(String text) {
        super(SERVER_MESSAGE);
        this.text = text;
    }

    public String asString() {
        return "----->>> " + text;
    }

    public String getText() {
        return text;
    }
}
