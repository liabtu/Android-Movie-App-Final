# TMDB Movie App with MVVM

თანამედროვე Android აპლიკაცია ფილმების აღმოჩენისა და სამართავად, რომელიც იყენებს TMDB API-ს.

## რა არის გაკეთებული

- **Dark / Light თემა** — Switch-ით გადართვა   
- **ფილმების სიები** — ახალი და პოპულარული (infinite scroll / pagination)  
- **ძებნა** — real-time და submit-ით  
- **ფილმის დეტალები** — სათაური, აღწერა, რეიტინგი, გამოსვლის თარიღი, ჟანრები  
- **Watchlist (საყვარელი ფილმები)** — Room Database-ში შენახვა/წაშლა, რეალურ დროში განახლება  
- **ჟანრების ფილტრაცია** — Home-ზე ჰორიზონტალური Chip-ები (discover API)  
- **სურათების ჩატვირთვა** — Glide + placeholder/error  

## ტექნოლოგიები

- Language: Kotlin  
- Architecture: MVVM(Model-View-ViewModel) + Repository pattern  
- Networking: Retrofit + Gson (TMDB API)  
- Database: Room Database(Local storage)
- Navigation Component + Safe Args  
- ViewBinding  
- Image Loading: Glide  
- UI: Material 3 + DayNight თემა  
- Async: LiveData / Flow  

## ეკრანები

1. **Home** — Featured + ახალი + პოპულარული + ჟანრების ფილტრი + თემის Switch  
2. **Search** — ფილმების ძებნა  
3. **Watchlist** — საყვარელი ფილმები (ცარიელი შემთხვევაში შეტყობინება)  
4. **Details** — ფილმის სრული ინფო + ფავორიტის ღილაკი

## სკრინშოტები
Home (Dark Mode)
![IMG_0719](https://github.com/user-attachments/assets/ffb8665f-7180-4e49-8b0a-d1e21c332373)
Details Screen (Dark Theme)
![IMG_0721](https://github.com/user-attachments/assets/894233b7-5d04-4630-9bf8-c46b5fb74a4a)
Watchlist – საყვარელი ფილმები
![IMG_0720](https://github.com/user-attachments/assets/20f5864c-e03b-4dce-9c55-cb345cc7c350)
Search Results (Dark)
![IMG_0718](https://github.com/user-attachments/assets/35f4621e-438d-4318-af2a-19b6d61971db)

1. Clone the repository:
git clone [https://github.com/liabtu/Android-Movie-App-Final.git](https://github.com/liabtu/Android-Movie-App-Final.git)
2. შექმენით `local.properties` ფაილი root-ში და ჩაწერეთ თქვენი TMDB_API_KEY=თქვენი_კოდი_აქ.
3.Sync Gradle and run the app
