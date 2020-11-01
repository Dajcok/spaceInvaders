package com.spaceinvaders;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Vector;

class projectile {
    RectangleShape projectile = new RectangleShape();
    private Vector2f size;
    private Vector2f position;

    projectile(Vector2f size, Vector2f position) {
        this.position = position;
        this.size = size;

        this.projectile.setSize(this.size);
        this.projectile.setPosition(this.position);
        this.projectile.setOrigin(new Vector2f(this.size.x / 2, this.size.y));
    }

    void updatePosition(Vector2f position) {
        this.position = position;
        this.projectile.setPosition(this.position);
    }

    Vector2f getPosition()
    {
        return this.position;
    }
}

class texturesLoader
{
    private Texture texture = new Texture();
    Sprite sprite = null;
    private boolean textureLoaded;
    private Vector2f textureScale;
    private Vector2f spritePos;

    texturesLoader(String filePath, Vector2f textureScale)
    {
        this.textureScale = textureScale;

        try
        {
            this.texture.loadFromFile(Paths.get(filePath));
            System.out.println("Textúra " + filePath + " úspešne načítaná");

            this.textureLoaded = true;

        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            this.textureLoaded = false;
        }
    }

    Sprite createSprite()
    {
        if(this.textureLoaded)
        {
            Sprite sprite = new Sprite(this.texture);
            sprite.setScale(this.textureScale);
            return sprite;
        }
        else
        {
            System.out.println("Sprite z textúry nemohol byť vytvorený");

            return null;
        }
    }

    boolean updatePosition(Vector2f position)
    {
        this.spritePos = position;

        if(this.sprite == null)
        {
            this.sprite = createSprite();
            this.sprite.setOrigin(Vector2f.div(new Vector2f(texture.getSize()), 2));
            return false;
        }

        else
        {
            this.sprite.setPosition(this.spritePos);
            return true;
        }
    }

    Vector2f getSpritePos()
    {
        return this.spritePos;
    }
}

class targetWindow
{
    private boolean changeDir = false;

    targetWindow(int W, int H, int frameLimit)
    {
        int enemies1PosX = 0;
        int enemies2PosX = 0;
        int enemies3PosX = 0;

        boolean projectileFired = false;

        projectile myProjectile = null;
        RenderWindow window = new RenderWindow();
        window.create(new VideoMode(W, H), "Space Invaders");
        window.setFramerateLimit(frameLimit);

        texturesLoader ship = new texturesLoader("ship.png", new Vector2f(0.2f, 0.2f));
        while(!ship.updatePosition(new Vector2f(100.f, 1000.f)));


        Vector<texturesLoader> enemies1 = new Vector<>();

        for(int i = 0; i < 18; i++)
        {
            enemies1.addElement(new texturesLoader("enemy1.png", new Vector2f(0.2f, 0.2f)));
            while(!enemies1.elementAt(i).updatePosition(new Vector2f(100.f + enemies1PosX, 400.f)));
            enemies1PosX += 100;
        }

        Vector<texturesLoader> enemies2 = new Vector<>();

        for(int i = 0; i < 18; i++)
        {
            enemies2.addElement(new texturesLoader("enemy1.png", new Vector2f(0.2f, 0.2f)));
            while(!enemies2.elementAt(i).updatePosition(new Vector2f(100.f + enemies2PosX, 300.f)));
            enemies2PosX += 100;
        }

        Vector<texturesLoader> enemies3 = new Vector<>();

        for(int i = 0; i < 18; i++)
        {
            enemies3.addElement(new texturesLoader("enemy1.png", new Vector2f(0.2f, 0.2f)));
            while(!enemies3.elementAt(i).updatePosition(new Vector2f(100.f + enemies3PosX, 200.f)));
            enemies3PosX += 100;
        }

        Vector<texturesLoader> allEnemies = new Vector<>();

        for (int i = 0; i < 18; i++)
        {
            allEnemies.addElement(enemies1.elementAt(i));
            allEnemies.addElement(enemies2.elementAt(i));
            allEnemies.addElement(enemies3.elementAt(i));
        }



        while(window.isOpen())
        {
            window.clear(Color.BLACK);
            window.draw(ship.sprite);
            for(int i = 0; i < allEnemies.size(); i++)
                window.draw(allEnemies.elementAt(i).sprite);
            if(myProjectile != null)
                window.draw(myProjectile.projectile);
            window.display();


            if(projectileFired && myProjectile != null)
            {
                myProjectile.updatePosition(new Vector2f(myProjectile.getPosition().x, myProjectile.getPosition().y - 50));
                if(myProjectile.getPosition().y < 0)
                    projectileFired = false;
            }


            if(myProjectile != null)
            {
                A:
                for(int i = 0; i < allEnemies.size() + 1; i++)
                {
                    if (allEnemies.size() != i && hitCheck(myProjectile.getPosition(), allEnemies.elementAt(i).getSpritePos()))
                    {
                        myProjectile = null;
                        projectileFired = false;
                        allEnemies.remove(i);
                        break A;
                    }

                }
            }

            enemyMovement(allEnemies);

            for(Event event : window.pollEvents())
            {
                switch(event.type)
                {
                    case CLOSED:
                        window.close();
                        break;

                    case KEY_PRESSED:
                        KeyEvent keyEvent = event.asKeyEvent();
                        if(keyEvent.key == Keyboard.Key.RIGHT && ship.getSpritePos().x < 1850)
                            ship.updatePosition(new Vector2f(ship.getSpritePos().x + 20.f, ship.getSpritePos().y));
                        else if(keyEvent.key == Keyboard.Key.LEFT && ship.getSpritePos().x > 100)
                            ship.updatePosition(new Vector2f(ship.getSpritePos().x - 20.f, ship.getSpritePos().y));
                        else if(keyEvent.key == Keyboard.Key.UP && !projectileFired)
                        {
                            myProjectile = new projectile(new Vector2f(5, 50), ship.getSpritePos());
                            projectileFired = true;
                        }
                        break;
                }
                if(event.type == Event.Type.CLOSED)
                {
                    window.close();
                }
            }
        }
    }

    boolean hitCheck(Vector2f bulletPos, Vector2f targetPos)
    {
        if((bulletPos.x > targetPos.x - 25 && bulletPos.y > targetPos.y - 25) && (bulletPos.x < targetPos.x + 25 && bulletPos.y < targetPos.y + 25))
            return true;
        else
            return false;
    }

    void enemyMovement(Vector<texturesLoader> enemies)
    {
        OUT:
        for(int i = 0; i < enemies.size(); i++)
        {
            if(enemies.elementAt(i).getSpritePos().x > 1870)//po znizeni nejde hitovat
            {
                enemyMovement_direction(i, enemies);
                for(int j = 0; j < enemies.size() ; j++)
                    enemies.elementAt(j).updatePosition(new Vector2f(enemies.elementAt(j).getSpritePos().x - (27.f / enemies.size()), enemies.elementAt(j).getSpritePos().y + 10));

                this.changeDir = true;

                break OUT;
            }

            else if(enemies.elementAt(i).getSpritePos().x < 30)
            {
                enemyMovement_direction(i, enemies);
                for(int j = 0; j < enemies.size(); j++)
                    enemies.elementAt(j).updatePosition(new Vector2f(enemies.elementAt(j).getSpritePos().x + (27.f / enemies.size()), enemies.elementAt(j).getSpritePos().y + 10));

                this.changeDir = false;

                break OUT;
            }
            else
                enemyMovement_direction(i, enemies);
        }
    }

    void enemyMovement_direction(int i, Vector<texturesLoader> enemies)
    {
        if(this.changeDir)
        {
            enemies.elementAt(i).updatePosition(new Vector2f (enemies.elementAt(i).getSpritePos().x - (27.f / enemies.size()), enemies.elementAt(i).getSpritePos().y));
        }

        else
        {
            enemies.elementAt(i).updatePosition(new Vector2f (enemies.elementAt(i).getSpritePos().x + (27.f / enemies.size()), enemies.elementAt(i).getSpritePos().y));
        }
    }
}

public class SpaceInvaders
{
    public static void main(String[] args)
    {
        targetWindow window = new targetWindow(1920, 1080, 60);
    }
}
