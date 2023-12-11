import sqlite3
import traceback
from kivy.app import App
from kivy.core.window import Window
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.recycleview import RecycleView
from kivy.uix.recycleview.views import RecycleDataViewBehavior
from kivy.uix.label import Label
from kivy.uix.button import Button
from kivy.uix.scrollview import ScrollView
from kivy.uix.textinput import TextInput
from kivy.properties import StringProperty
from kivy.app import App
from kivy.uix.screenmanager import ScreenManager, Screen
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.button import Button
a = []
class FirstScreen(Screen):
    def on_enter(self, **kwargs):
        super(FirstScreen, self).__init__(**kwargs)
        self.layout = BoxLayout(orientation='vertical')
        self.text_input = TextInput(size_hint=(1, 0.1))
        self.text_input.bind(text=self.text)
        self.button = Button(text='Добавить книгу', size_hint=(1, 0.1))
        self.button.bind(on_press=self.secondscreen)
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute('''CREATE TABLE IF NOT EXISTS bookdata
                                  (id INTEGER PRIMARY KEY, name TEXT,athor TEXT, desc TEXT, janrs TEXT)''')
        conn.commit()
        self.x0 = cursor.execute("SELECT * FROM bookdata ").fetchall()
        conn.close()
        scroll_view = ScrollView(size_hint=(1, 0.5), size=(Window.width, Window.height))
        self.layout0 = BoxLayout(orientation='vertical', size_hint=(1, 0.5))
        for i in self.x0:
            b = Button(text=i[1]+'\n'+i[2])
            self.layout0.add_widget(b)
            b.bind(on_press=lambda instance: self.click(i))
        scroll_view.add_widget(self.layout0)
        self.layout.add_widget(self.text_input)
        self.layout.add_widget(self.button)
        self.layout.add_widget(scroll_view)
        self.add_widget(self.layout)
    def click(self,x):
        self.manager.transition.direction = 'left'
        self.manager.current = 'second'
        self.manager.get_screen('second')
        a.clear()
        a.append(x)
    def secondscreen(self,instance):
        self.manager.transition.direction = 'left'
        self.manager.current = 'second'
        self.manager.get_screen('second')
        a.clear()
        a.append([0,'','','',''])
    def text(self,instance,value):
        self.layout0.clear_widgets()
        for i in self.x0:
            if value in i[1] or value in i[2] or value in i[3] or value in i[4] or value == '':
                b = Button(text=i[1]+'\n'+i[2])
                self.layout0.add_widget(b)
                b.bind(on_press=lambda instance: self.click(i))
class SecondScreen(Screen):
    def on_enter(self, **kwargs):
        super(SecondScreen, self).__init__(**kwargs)
        try:
            self.s = a[0]
        except:
            self.s = [0,'','','','']
        self.layout = BoxLayout(orientation='vertical')
        self.button = Button(text="Назад", size_hint=(1, 0.1))
        self.button.bind(on_press=self.click)
        self.ename = TextInput(text='Имя', size_hint=(1, 0.1))
        self.eathor = TextInput(text='Автор', size_hint=(1, 0.1))
        self.edesc = TextInput(text='Описание', size_hint=(1, 0.1))
        self.ejanrs = TextInput(text='Жанры', size_hint=(1, 0.1))
        self.save = Button(text='Сохранить', size_hint=(1, 0.1))
        self.save.bind(on_press=self.savebook)
        if self.s[1] != '':
            self.ename.text = self.s[1]
            self.eathor.text = self.s[2]
            self.edesc.text = self.s[3]
            self.ejanrs.text = self.s[4]
        self.layout.add_widget(self.button)
        self.layout.add_widget(self.ename)
        self.layout.add_widget(self.eathor)
        self.layout.add_widget(self.edesc)
        self.layout.add_widget(self.ejanrs)
        self.layout.add_widget(self.save)
        self.add_widget(self.layout)
    def savebook(self,instance):
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        if self.ename not in ['','Имя'] and self.eathor not in ['','Автор'] and self.edesc not in ['','Описание'] and self.ejanrs not in ['','Жанры']:
            if self.s != [0,'','','','']:
                cursor.execute("UPDATE bookdata SET name = ?,athor = ?,desc = ?,janrs = ? WHERE id = ?",
                               (self.ename.text, self.eathor.text, self.edesc.text, self.ejanrs.text, self.s[0]))
            else:
                cursor.execute(
                    "INSERT INTO bookdata (name,athor,desc,janrs) VALUES (?, ?, ?, ?)",
                    (self.ename.text, self.eathor.text, self.edesc.text, self.ejanrs.text))
        conn.commit()
        conn.close()
    def click(self,instance):
        for child in self.children[:]:
            app = App.get_running_app()
            app.root.transition.direction = 'right'
            app.root.current = 'first'
            # Здесь нужно передать информацию в SecondScreen
        return False
class MyKivyApp(App):
    def build(self):
        sm = ScreenManager()
        sm.add_widget(FirstScreen(name='first'))
        sm.add_widget(SecondScreen(name='second'))
        return sm

if __name__ == '__main__':
    MyKivyApp().run()
