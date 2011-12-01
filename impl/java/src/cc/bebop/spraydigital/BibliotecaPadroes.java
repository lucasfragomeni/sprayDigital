package cc.bebop.spraydigital;

import java.util.Arrays;

import processing.core.PApplet;

public class BibliotecaPadroes {

	private final int RAIO_MIN;
	private final int RAIO_MAX;
	private final int PADROES;
	private final int DENSIDADE_MAX;

	private PApplet pApplet;

	//[raios][padroes][moveX|moveY]
	private float movesX[][][];
	private float movesY[][][];

	private float minopac = -15F;
	private float maxopac = 15F;
	private float moveX[];
	private float moveY[];
	private float opacChange[];

	public BibliotecaPadroes(PApplet pApplet, int raioMin, int raioMax, int padroes, int densidadeMax) {
		this.pApplet = pApplet;

		RAIO_MIN = raioMin;
		RAIO_MAX = raioMax;
		PADROES = padroes;
		DENSIDADE_MAX = densidadeMax;

		movesX = new float[RAIO_MAX][PADROES][];
		movesY = new float[RAIO_MAX][PADROES][];

		moveX = new float[DENSIDADE_MAX];
		moveY = new float[DENSIDADE_MAX];
		opacChange = new float[DENSIDADE_MAX];

		criarBiblioteca();
	}

	private void criarBiblioteca() {
		//Raios
		for(int r = RAIO_MIN; r < movesX.length; r++) {
			//Padroes
			for(int p = 0; p < movesX[r].length; p++) {
				//Concentrado
				float angulo = 0;//Inicial 0
				float incremento = 2;//Incremento 2

				//Itera pelas 2800 pontas do spray
				float raioIncremental = 0;
				for(int i = 0; i < DENSIDADE_MAX; i++) {
					//Para cada ponta da escova
					float anguloRadianos = PApplet.radians(angulo);
					float raioTmp = pApplet.random(raioIncremental, r);
					if(i % 2 == 0) {
						raioTmp *= -1;
					}
					moveX[i] = (float) (Math.cos(anguloRadianos) * raioTmp);
					moveY[i] = (float) (Math.sin(anguloRadianos) * raioTmp);
					opacChange[i] = pApplet.random(minopac, maxopac);

					angulo += incremento;
					if(i % 100 == 0) {
						raioIncremental++;
					}
				}

				//raio: 0 - padrao: 0 - movesX: x, y, z
				//raio: 0 - padrao: 1 - movesX: x1, y1, z1
				//raio: 0 - padrao: 2 - movesX: x2, y2, z2
				//...
				//raio: 0 - padrao: 49 - movesX: x49, y49, z49
				//raio: 1 - padrao: 0 - movesX: x, y, z
				movesX[r][p] = Arrays.copyOf(moveX, moveX.length);
				movesY[r][p] = Arrays.copyOf(moveY, moveY.length);
			}
		}
	}

	public void randomizarPadrao(int raio) {
		int padrao = Math.min((int)pApplet.random(0, movesX[raio].length-1), PADROES-1);

		moveX = movesX[raio][padrao];
		moveY = movesY[raio][padrao];
	}

	public float[] getMoveX() {
		return moveX;
	}

	public float[] getMoveY() {
		return moveY;
	}

	public float[] getOpacChange() {
		return opacChange;
	}

}