﻿using System;
using System.Collections.Generic;
using System.Text;
using System.Collections;
using CommLayer.Messages;

namespace CommLayer
{
    /// <summary>
    /// Lista de mensajes que asegura busqueda en orden constante
    /// </summary>
    internal class MessageList
    {
        /// <summary>
        /// La tabla de hashing de la colección
        /// </summary>
        private Hashtable messageCollection;

        /// <summary>
        /// Candado para control de threading
        /// </summary>
        private Object thisLock;

        /// <summary>
        /// Default Constructor
        /// </summary>
        public MessageList()
        {
            messageCollection = new Hashtable();
            thisLock = new Object();
        }

        /// <summary>
        /// Agrega un mensaje a la lista
        /// </summary>
        /// <param name="message">El mensaje a agregar</param>
        public void add(Message message) 
        {
            lock (thisLock)
            {
                //Si no tengo el ip, entonces agrego al usuario como alguien nuevo
                if (!messageCollection.Contains(message.Id))
                {
                    messageCollection.Add(message.Id, message);
                }
                //Si ya la tengo, actualizo el objeto usuario
                else
                {
                    messageCollection.Remove(message.Id);
                    messageCollection.Add(message.Id, message);
                } 
            }
        }

        /// <summary>
        /// Remueve un mensaje de la lista
        /// </summary>
        /// <param name="id">El id del mensaje a remover</param>
        /// <returns>true si existia y fue removido, false si no</returns>
        public bool remove(Guid id)
        {
            lock (thisLock)
            {
                if (messageCollection.Contains(id))
                {
                    messageCollection.Remove(id);
                    return true;
                }
                else
                {
                    return false;
                } 
            }
        }

        /// <summary>
        /// Obtiene un mensaje de la lista con busqueda en orden constante
        /// </summary>
        /// <param name="id">el id del mensaje a buscar</param>
        /// <returns>El mensaje de la lista, null si no existía</returns>
        public Message getMessage(Guid id)
        {
            lock (thisLock)
            {
                Object o = messageCollection[id];
                if (o != null)
                {
                    return (Message)o;
                }
                else
                {
                    return null;
                } 
            }
        }

        /// <summary>
        /// fabrica un array con la lista de mensajes
        /// </summary>
        /// <returns>Un array de los mensajes listados</returns>
        public Message[] messageListToArray()
        {
            lock (thisLock)
            {
                Message[] us = new Message[messageCollection.Count];
                IDictionaryEnumerator en = messageCollection.GetEnumerator();
                int i = 0;
                while (en.MoveNext())
                {
                    us[i] = (Message)en.Value;
                    i++;
                }
                return us; 
            }
        }

        /// <summary>
        /// Calcula el tamaño de la lista
        /// </summary>
        /// <returns>El tamaño de la lista</returns>
        public int size()
        {
            lock (thisLock)
            {
                return messageCollection.Count; 
            }
        }
    }
}
