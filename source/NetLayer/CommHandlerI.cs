using System;
using System.Collections.Generic;
using System.Text;

namespace NetLayer
{
    /// <summary>
    /// Interfaz para eventos de comunicaci�n generados en NetHandler
    /// </summary>
    public interface CommHandlerI
    {
        /// <summary>
        /// Se gatilla cuando debe comenzar la comunicaci�n
        /// </summary>
        void startNetworkingHandler();
        /// <summary>
        /// Se gatilla cuando se debe detener la comunicaci�n 
        /// </summary>
        void stopNetworkingHandler();
        /// <summary>
        /// Se gatilla cuando se resetear� la coneci�n
        /// </summary>
        void resetNetworkingHandler();
        /// <summary>
        /// Se gatilla cuando ocurre un error en la red
        /// </summary>
        /// <param name="e">La excepci�n generada</param>
        void errorNetworkingHandler(Exception e);
        /// <summary>
        /// Se gatilla para enviar informaci�n del estado de la red.
        /// </summary>
        /// <param name="message">El mensaje env�ado</param>
        void informationNetworkingHandler(String message);
    }
}
