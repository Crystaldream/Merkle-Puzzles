
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Merkle {
	
	public static byte[] gerarChave() {
		
		SecureRandom random = new SecureRandom();
		int index = 12;
		
		byte[] chave = new byte[16];
		
		for(int i = 0; i < 16; i++){
			
			if(i > 11){
				
				byte[] tmp = new byte[1];
				random.nextBytes(tmp);
				
				chave[index] = tmp[0];
				index++;
				
			}else{
				
				chave[i] = 0;
				
			}

		}
		
		return chave;
	
	}
	
	
	public static String gerarRandomByte() {
		
		SecureRandom random = new SecureRandom();
		
		byte[] rand = new byte[16];
		String byteArray = "";
		
		for(int i = 0; i < 16; i++){
			
			byte[] tmp = new byte[1];

			random.nextBytes(tmp);

			if((int)tmp[0] < 0){
				tmp[0] += 128;
			}
			
			rand[i] = tmp[0];
			byteArray = byteArray + Integer.toBinaryString(rand[i]);
			
		}
		
		return byteArray;
		
	}
	
	
	public static String cifrarPuzzle(String puzzleId, byte[] chave) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		
		Cipher cifra = Cipher.getInstance("AES");
		
		SecretKeySpec secret = new SecretKeySpec(chave, "AES");
		
		cifra.init(Cipher.ENCRYPT_MODE, secret);
		
		//System.out.println(puzzleId);
		String puzzleCifrado = Base64.encodeBase64String(cifra.doFinal(puzzleId.getBytes()));
		
		return puzzleCifrado;
		
	}
	
	
	public static String decifrarPuzzle(String randomPuzzle) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		byte[] chave = new byte[16];
		
		for(int c = 0; c < 12; c++){
			chave[c] = 0;
		}
		
		String puzzleDecifrado = "";
		
		for(int i = -128; i < 128; i++){
			for(int j = -128; j < 128; j++){
				for(int k = -128; k < 128; k++){
					for(int l = -128; l < 128; l++){
						
						chave[12] = (byte) i;
						chave[13] = (byte) j;
						chave[14] = (byte) k;
						chave[15] = (byte) l;
						
						Cipher cifra = Cipher.getInstance("AES/ECB/NoPadding");
						
						SecretKeySpec secret = new SecretKeySpec(chave, "AES");
						
						cifra.init(Cipher.DECRYPT_MODE, secret);
						
						puzzleDecifrado = new String(cifra.doFinal(Base64.decodeBase64(randomPuzzle)));
						
					}
				}
			}
		}
		
		return puzzleDecifrado;
		
	}
	
	
	public static String randomPuzzleId(HashSet<String> puzzlesFicheiro){
		
		SecureRandom rand = new SecureRandom();
		
		// gerar numero entre 0 e o tamanho do hashset
		int item = rand.nextInt(puzzlesFicheiro.size());
		int posicao = 0;
		String stringAleatoria = "";

		Iterator<String> iterador = puzzlesFicheiro.iterator();

		while(iterador.hasNext()){
			
			stringAleatoria = iterador.next();
			if(posicao == item){
				
				return stringAleatoria;
				
			}
			
			posicao++;
			
		}
		
		return null;
		
	}
	
	
	public static void salvarFicheiroPuzzles(HashSet<String> puzzlesCifrados) throws FileNotFoundException, UnsupportedEncodingException{
		
		PrintWriter ficheiro = new PrintWriter("merkle.mkl", "UTF-8");
		
		System.out.println("\nA escrever dados no ficheiro...");
		Iterator<String> it = puzzlesCifrados.iterator();
		while(it.hasNext()){
			ficheiro.println(it.next() + "\n");
		}
		
		ficheiro.close();
		
		System.out.println(".:: Ficheiro criado com sucesso ::.");
		
	}
	
	
	public static void salvarFicheiroSecretAlice(String idFinal) throws FileNotFoundException, UnsupportedEncodingException{
		
		PrintWriter ficheiro = new PrintWriter("secretAlice.mkl", "UTF-8");
		
		System.out.println("\nA escrever dados no ficheiro...");

		ficheiro.write(idFinal);
		
		ficheiro.close();
		
		System.out.println(".:: Ficheiro criado com sucesso ::.");
		
	}
	
	
	public static void salvarFicheiroSecretBob(String finalXKey) throws FileNotFoundException, UnsupportedEncodingException{
		
		PrintWriter ficheiro = new PrintWriter("secretBob.mkl", "UTF-8");
		
		System.out.println("\nA escrever dados no ficheiro...");

		ficheiro.write(finalXKey);
		
		ficheiro.close();
		
		System.out.println(".:: Ficheiro criado com sucesso ::.");
		
	}
	
	
	public static HashSet<String> lerFicheiroPuzzles() throws IOException{
		
		HashSet<String> puzzlesFicheiro = new HashSet<String>();
		
		File ficheiro = new File("MerkleTransferido.mkl");
		
		try{
			
			Scanner scn = new Scanner(ficheiro);
			
			while(scn.hasNextLine()){
				String linha = scn.nextLine();
				puzzlesFicheiro.add(linha);
			}
			
			scn.close();
			
		}catch(FileNotFoundException e){
			System.out.println(e);
		}
		
		return puzzlesFicheiro;
		
	}
	
	
	public static String removerLixoPrimeiraPos(String s){
		
		String stringSemEspacos = "";
		
		for(int i = 1; i < s.length(); i++){
			stringSemEspacos = stringSemEspacos + s.charAt(i);
		}
		
		return stringSemEspacos;
		
	}
	
	
	public static String extract(HashSet<String> puzzlesOriginais, String novoId_x){
		
		String aux = "";
		
		Iterator<String> iterador = puzzlesOriginais.iterator();
		while(iterador.hasNext()){
			
			aux = iterador.next();
				
			int aspas = 0;
			int espacos = 0;
			int caracterId = 0;
			int caracterChave = 0;
				
			String id_x = "";
			String chave_x = "";
			
			for(int x = 0; x < aux.length(); x++){
				
				if(x == 0){
					caracterId++;
				}
				
				if(aspas == 2){
					caracterChave++;
				}
				
				if(aux.charAt(x) == '"'){
					aspas++;
				}
					
				if(aux.charAt(x) == ' '){
					espacos++;
				}
				
				if((caracterId == 1) &&(espacos == 2 && aspas < 2)){
					id_x = id_x + aux.charAt(x);
				}
					
				if((caracterChave == 1) && (aspas == 2)){
					chave_x = chave_x + aux.charAt(x);
				}
				
			}
			
			if(id_x.equals(novoId_x)){
				return chave_x;
			}
			
		}
		
		return aux;
		
	}
	
	
	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException {
		
		int num_bits = 0;
		int porta = 43210;
		byte[] chave = gerarChave();
		
		// Inicializar HashSets
		HashSet<String> puzzlesCifrados = new HashSet<String>();
		HashSet<String> puzzlesOriginais = new HashSet<String>();
		HashSet<String> puzzlesFicheiro = new HashSet<String>();
		
		//System.out.println(Arrays.toString(chave));
		
		int modo = 0;
		
		if(args[0].equals("-Alice")){
			modo = 1;
		}else if(args[0].equals("-Bob")){
			modo = 2;
		}
		
		if(args.length > 1){
			num_bits = Integer.parseInt(args[1]);
		}
		
		if(num_bits < 10 || num_bits > 31){
			System.out.println("Numero inválido de bits (Entre 10 e 31 inclusivé)\nO Programa foi encerrado");
			System.exit(1);
		}
		
		// Gerar chave de cifra
		
		
		//System.out.println(Arrays.toString(chave));
		
		if(modo == 1){
			
			// Servidor
			ServerSocket socketServidor = null;
			
			try{
				socketServidor = new ServerSocket(porta);
				System.out.println("\n\t\t.:: Bem vindo/a ::.\n\nServidor inicializado, à espera de ligações na porta " + porta);
				
				// Gerar puzzles
				
				System.out.println("\nA gerar " + (int)Math.pow(2, num_bits) + " Puzzles...por favor aguarde");
				
				for(int i = 1; i < (int)Math.pow(2, num_bits); i++){

					// Inicializar Strings
					String puzzleId = "";
					String id_x = gerarRandomByte();
					String chave_k = gerarRandomByte();
					
					puzzleId = "\"Puzzle # " + id_x + "\"" + chave_k;
					
					// Adicionar puzzle a um hashset para posterior verificacao na decifragem
					puzzlesOriginais.add(puzzleId);
					
					// Cifrar puzzle
					String criptograma = cifrarPuzzle(puzzleId, chave);

					// Adicionar criptograma ao HashSet puzzlesCifrados
					puzzlesCifrados.add(criptograma);
					
				}
				
				System.out.println(".:: Puzzles gerados com sucesso ::.");
				
				// Guardar os puzzles Cifrados no ficheiro merkle.mkl
				try {
					salvarFicheiroPuzzles(puzzlesCifrados);
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					System.out.println(e);
				}

			}catch (IOException e){
				System.out.println(e);
			}
			
			try{
				Socket sessao = socketServidor.accept();
				ServerSocket s = new ServerSocket(porta);
				//Socket sessao2 = s.accept();
				
				System.out.println("\n-> Bob entrou no sistema. IP: " + sessao.getInetAddress().getHostAddress() + "\n");
				
				System.out.println("A enviar o ficheiro...");
				
				File f = new File ("merkle.mkl");
	            byte [] ficheiroBytes  = new byte [(int) f.length()];
	            
	            FileInputStream fis = new FileInputStream(f);
	            BufferedInputStream bis = new BufferedInputStream(fis);

	            bis.read(ficheiroBytes, 0, ficheiroBytes.length);
	            OutputStream os = sessao.getOutputStream();
	            
	            os.write(ficheiroBytes, 0, ficheiroBytes.length);
	            os.flush();
				
				System.out.println("Ficheiro enviado com sucesso");
				
				/*
				InputStream inputS = sessao2.getInputStream();
				InputStreamReader inputStreamR = new InputStreamReader(inputS);
				BufferedReader bufReader = new BufferedReader(inputStreamR);
				
				String idFinal = bufReader.readLine();
				
				salvarFicheiroSecretAlice(idFinal);				
				*/
				
				sessao.close();
				bis.close();
				socketServidor.close();
				s.close();
				
			}catch (IOException e){
				System.out.println(e);
			}

		}
		
		if(modo == 2){
			
			// Cliente

			InetAddress enderecoIP;
			enderecoIP = InetAddress.getByName("127.0.0.1");
			Socket cliente = new Socket(enderecoIP, porta);
			
	        int tamanho = 1048576; // 20 bits -> 2^20
	        int bytesLidos = 0;
	        int totalTamanhoFicheiro = 0;
	        
	        System.out.println("A receber ficheiro...");
	        
	        byte [] ficheiroBytes  = new byte [tamanho];
	        
	        InputStream is = cliente.getInputStream();
	        FileOutputStream fos = new FileOutputStream("merkleTransferido.mkl");
	        BufferedOutputStream bos = new BufferedOutputStream(fos);
	        
	        bytesLidos = is.read(ficheiroBytes, 0, ficheiroBytes.length);
	        totalTamanhoFicheiro = bytesLidos;
	 
	        do {
	        	
	        	bytesLidos = is.read(ficheiroBytes, totalTamanhoFicheiro, (ficheiroBytes.length - totalTamanhoFicheiro));
	        	
	        	if(bytesLidos >= 0){
	        		totalTamanhoFicheiro += bytesLidos;
	        	}
	        	
	        } while(bytesLidos > -1);
	 
	        bos.write(ficheiroBytes, 0 , totalTamanhoFicheiro);
	        bos.flush();
	        bos.close();
	        
			System.out.println("Ficheiro recebido com sucesso");
			
			// ler ficheiro
			puzzlesFicheiro = lerFicheiroPuzzles();
			
			// escolher puzzle aleatorio
			String randomPuzzle = randomPuzzleId(puzzlesFicheiro);
			
			// decifrar o puzzle
			String randomPuzzleDecifrado = decifrarPuzzle(randomPuzzle);
			
			System.out.println("-> " + randomPuzzleDecifrado);
			
			// encontrar novo x e novo k
			int aspas = 0;
			int espacos = 0;
			
			String novoId_x = "";
			String novaChave_x = "";
			
			for(int x = 0; x < randomPuzzleDecifrado.length(); x++){
				
				if(randomPuzzleDecifrado.charAt(x) == '"'){
					aspas++;
				}
				
				if(randomPuzzleDecifrado.charAt(x) == ' '){
					espacos++;
				}
				
				if(espacos == 2 && aspas < 2){
					novoId_x = novoId_x + randomPuzzleDecifrado.charAt(x);
				}
				
				if(aspas == 2){
					novaChave_x = novaChave_x + randomPuzzleDecifrado.charAt(x);
				}
				
			}
			
			/*
			String finalXKey = extract(puzzlesOriginais, novoId_x);
			
			Socket cliente2 = new Socket(enderecoIP, porta);
			OutputStream output = cliente2.getOutputStream();
			OutputStreamWriter outputw = new OutputStreamWriter(output);
			BufferedWriter bw = new BufferedWriter(outputw);
			bw.write(finalXKey);
			
			salvarFicheiroSecretBob(finalXKey);
			*/
			
			cliente.close();
			//cliente2.close();
			
		}
		
	}
	
}