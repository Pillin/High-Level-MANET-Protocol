using System;
using System.Collections.Generic;
using System.Text;
using System.Collections;

namespace CommLayer
{
    /// <summary>
    /// Colecci�n de id's de mensajes.
    /// Los ids son unicos en la colecci�n
    /// Si se agrega un id cuando el tama�o es igual a MaxSize, se borra el primero en la cola antes de agregar
    /// </summary>
    internal class MessageIdCollection
    {
        /// <summary>
        /// Tabla de hashing para busqueda de orden constante
        /// </summary>
        private Hashtable messageIdList;

        /// <summary>
        /// Cola de prioridad para entrada y salida en orden constante
        /// </summary>
        private Queue<Guid> messageIdqueue;

        /// <summary>
        /// Control de threeading
        /// </summary>
        private Object thisLock;

        /// <summary>
        /// El tama�o m�ximo de la colecci�n
        /// </summary>
        private Int32 maxSize;        

        /// <summary>
        /// Default Constructor
        /// </summary>
        public MessageIdCollection()
        {
            thisLock = new Object();
            messageIdList = new Hashtable();
            messageIdqueue = new Queue<Guid>();
            MaxSize = 100;
        }

        /// <summary>
        /// El tama�o m�ximo de la colecci�n
        /// </summary>
        public Int32 MaxSize
        {
            get { return maxSize; }
            set { maxSize = value; }
        }

        /// <summary>
        /// Coloca un id en la colecci�n
        /// </summary>
        /// <param name="id">El id a agregar a la colecci�n</param>
        public void add(Guid id)
        {
            lock (thisLock)
            {
                if (!messageIdList.Contains(id))
                {
                    if (messageIdqueue.Count >= MaxSize)
                    {
                        Guid deadId = messageIdqueue.Dequeue();
                        messageIdList.Remove(deadId);
                    }
                    messageIdList.Add(id, id);
                    messageIdqueue.Enqueue(id);
                }
            }
        }

        /// <summary>
        /// Retorna el tama�o de la cola
        /// </summary>
        /// <returns>El tama�o de la cola</returns>
        public int size()
        {
            lock (thisLock)
            {
                return messageIdqueue.Count;
            }
        }

        /// <summary>
        /// Verifica si existe un id en la colecci�n
        /// </summary>
        /// <param name="id">El id a buscar en la colecci�n</param>
        /// <returns>true si el id existe en la colecci�n, false si no</returns>
        public bool contains(Guid id)
        {
            lock (thisLock)
            {
                return messageIdList.Contains(id);
            }
        }
    }
}
