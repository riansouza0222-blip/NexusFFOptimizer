# Nexus FF Optimizer (Android / Xiaomi)

Projeto Android Studio em Kotlin, com `minSdk 23` e `compileSdk 35`.

## O que faz
- Mostra memória disponível reportada pelo Android.
- Mostra nível da bateria e, quando disponível, temperatura/estado térmico.
- Abre as configurações de acesso ao Não Perturbe.
- Localiza e abre Free Fire normal (`com.dts.freefireth`) ou Free Fire MAX (`com.dts.freefiremax`).
- Mantém a própria interface simples e leve.

## Limites importantes
O Android/MIUI não permite que um app comum aumente FPS, altere o escalonador da CPU/GPU, limpe RAM de outros apps de forma confiável ou mantenha otimizações privilegiadas em segundo plano. Por isso, este projeto não mata processos, não modifica o jogo e não promete ganhos artificiais. O botão Preparar celular atualiza os indicadores e orienta o uso de Não Perturbe.

## Como gerar o APK
1. Instale Android Studio no computador.
2. Extraia o ZIP e abra a pasta `NexusFFOptimizer` no Android Studio.
3. Aguarde a sincronização do Gradle (é necessário internet na primeira vez).
4. Conecte o Xiaomi por USB com Depuração USB ativada ou use um emulador.
5. Selecione **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. O APK de teste ficará em `app/build/outputs/apk/debug/app-debug.apk`.

## Instalação no Xiaomi
Transfira o APK para o celular, abra-o e autorize a instalação dessa origem quando o Android solicitar. Para reduzir restrições da MIUI durante o uso, também é possível permitir início automático e atividade em segundo plano nas configurações do próprio Xiaomi, se essas opções existirem no modelo/versão.
