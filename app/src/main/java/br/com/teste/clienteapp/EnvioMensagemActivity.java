package br.com.teste.clienteapp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class EnvioMensagemActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Enviar Mensagem");
        setContentView(R.layout.activity_envio_mensagem);

        Button botaoEnviarMensagem = findViewById(R.id.botao_enviar_mensagem);
        EditText mensagemEnviada = findViewById(R.id.editText_mensagem);


        botaoEnviarMensagem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String mensagem = mensagemEnviada.getText().toString();
                mensagemEnviada.setText("");
                new Thread(new Runnable() {
                    public void run() {
                        try(Socket socket = criarSocket()) {
                            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                                    botaoEnviarMensagem.getWindowToken(), 0);


                            PrintStream ps = enviarMensagem(socket, mensagem);

                            String resposta = receberResposta(socket);

                            Snackbar respostaSnackbar = Snackbar.make(findViewById(R.id.linear_view),
                                    resposta, Snackbar.LENGTH_LONG);
                            respostaSnackbar.show();

                            fecharConexao(socket, ps);
                        } catch (Exception e) {
                            Snackbar respostaSnackbar = Snackbar.make(findViewById(R.id.linear_view),
                                    "A Conexão foi Encerrada.", Snackbar.LENGTH_LONG);
                            respostaSnackbar.show();
                        }
                    }
                }).start();
            }
        });

    }

    private void fecharConexao(Socket socket, PrintStream ps) throws IOException {
        ps.close();
        socket.close();
    }

    private String receberResposta(Socket socket) throws IOException {
        Scanner respostaServidor = new Scanner(socket.getInputStream());
        String resposta = respostaServidor.nextLine();
        respostaServidor.close();
        return resposta;
    }

    private PrintStream enviarMensagem(Socket socket, String mensagem) throws IOException {
        PrintStream ps = new PrintStream(socket.getOutputStream());
        ps.println(mensagem);
        return ps;
    }

    private Socket criarSocket() throws IOException {
        Socket socket = new Socket("192.168.0.105", 30062);
        socket.setSoTimeout(15000);
        return socket;
    }
}
