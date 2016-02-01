#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

struct sockaddr_in serv_addr, client_addr;

void main()
{
    char buffer[1024000]; 
    int socket, file_des, pid, message_length, client_addr_len = sizeof(client_addr);
    
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = 8;
    // Local IP of base station goes here
    serv_addr.sin_addr.s_addr = inet_addr("192.168.1.105");
    
    file_des = socket(AF_INET, SOCK_STREAM, 0);
    bind(file_des, (struct sockaddr *) &serv_addr, sizeof(serv_addr));
    
    listen(file_des, 1);

    for( ; ; ) 
    {
        socket = accept(file_des, &client_addr, &client_addr_len);
        if( (pid = fork()) == 0 )
        {
            /// TODO: Transfer file containing node temperature data to client. 
            write(socket, buffer, message_length);
        }
        close(socket);
    }
}
