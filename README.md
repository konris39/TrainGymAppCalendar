# Welcome to my app TrainGymAppCalendar 🫡

Aplikacja służy do zapisywania swoich treningów oraz dodawania ich do kalendarza!

Strona Logowania:

![Logowanie](https://github.com/user-attachments/assets/19e1ffff-fcc1-453c-ba71-5d67b8da2bcc)

Strona Główna:

![MainPage](https://github.com/user-attachments/assets/0b42b2eb-fcd6-4dd5-9f80-dd0908f05eed)

Tu możemy dodać nowy event(trening):

![AddEvent](https://github.com/user-attachments/assets/137e45a3-7173-4fbb-a942-0e683b0c9641)

A tutaj mamy kalendarz z treningami:

![calendar](https://github.com/user-attachments/assets/dab99431-0fa6-46c8-a7f3-67115dd8721a)

A w tym miejscu, możemy dokładnie sprawdzić co mamy w treningu:

![yourWorkouts](https://github.com/user-attachments/assets/86c0f16d-75fd-4bcb-884b-69d8b2d041cd)

Możemy również obliczyć nasz prawdopodobny rekord na jedno powtórzenie:

![1rmcalc](https://github.com/user-attachments/assets/76f44a27-6fbd-4029-a189-d2e80f0c3a2b)

W zakładce profil mamy możliwość dodania informacji o nas, a także zobaczenie naszego wyniku w trójboju oraz BMI:

![profilPage](https://github.com/user-attachments/assets/03aad058-43dd-4de4-ad93-5ac3033bf2b1)

Źle stworzony event(trening) możemy edytować:

![editEvent](https://github.com/user-attachments/assets/e79d61e2-e914-48e1-9a38-4b7d68abbc28)


Jak zsetupować aplikacje?

# **Na backendzie:**
```bash
cd TrainGymAppCalendarBackend
```

Gdy budujesz poraz pierwszy:

```
docker compose up -d --build // build jedynie przy pierwszym wywołaniu, bądź gdy chcemy przebudować baze!!!
```

W przeciwnym przypadku, dla normalnego odpalenia:

```
docker compose up -d
```

*Oraz odapalmy TrainGymAppCalendarBackendApplication w naszym IDE*


# **Na frontendzie:**
```bash
cd train-gym-app-calendar-frontend
```

```
npm install
npm start
```
