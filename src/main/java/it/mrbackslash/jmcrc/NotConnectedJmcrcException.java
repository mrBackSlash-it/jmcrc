/*
This software is distributed under the Apache License 2.0
Copyright 2020 Vittorio Lo Mele

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
package it.mrbackslash.jmcrc;

/**
 * NotConnected Exception. Throwed when a method that needs a server connection is called, but the instance is not connected to any server.
 */
public class NotConnectedJmcrcException extends Exception{
    NotConnectedJmcrcException(){
        super("You are not connected to any server!");
    }
}
