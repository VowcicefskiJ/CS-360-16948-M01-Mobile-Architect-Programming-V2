# CS-360-16948-M01-Mobile-Architect-Programming-V2

# Inventory Management App - CS 360 Project Three  

## Overview  
This project is an **Inventory Management App** designed to help users track stock levels, manage inventory, and get alerts when items run low. The app was built using **Android Studio** with Java and XML, following best practices for mobile app development.  

## App Features  
- **User Authentication** – Users can sign up and log in securely.  
- **Inventory List** – Displays all items in stock with details like name, quantity, and description.  
- **Add, Update, and Delete Items** – Users can modify inventory in real time.  
- **Low Stock Notifications** – Alerts users when an item’s quantity reaches zero.  
- **Simple UI Design** – Focused on usability and easy navigation.  

## Development Approach  
To build this app, I:  
1. **Planned out the core features** before starting development.  
2. **Used a modular approach** by breaking the app into separate activities (login, inventory display, item management).  
3. **Tested frequently** to catch and fix bugs early.  

## Testing & Debugging  
- Ran the app in the emulator and on a physical device to check for **crashes and UI issues**.  
- Verified that **inventory updates sync correctly** in the database.  
- Ensured **notifications trigger properly** when items run low.  

## Challenges & Solutions  
One of the biggest challenges was **handling real-time database updates**. At first, changes didn’t appear instantly in the inventory list. I fixed this by **using Firebase’s real-time sync feature** to make sure updates showed immediately. Another issue was setting up **push notifications**, which required enabling the right Android permissions.  

## What I Learned  
This project helped me:  
- Understand **how to structure an Android app** from start to finish.  
- Improve **UI/UX design skills** by making the app easy to navigate.  
- Work with **real-time databases and notifications** to enhance functionality.  

## Future Improvements  
If I had more time, I’d:  
- Improve the UI with **better styling and animations**.  
- Add **barcode scanning** to make inventory management faster.  
- Implement **user roles** so managers and staff have different permissions.  

