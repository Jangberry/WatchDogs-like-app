import asyncio
import websockets

try:
    async def hello(websocket, path):
        global recieved
        global Return
        recieved = await websocket.recv()
        print("< {}".format(recieved))
        Return = "yolo"
        await websocket.send(Return)
        print("> {}".format(Return))

    start_server = websockets.serve(hello, 'localhost', 8765)

    asyncio.get_event_loop().run_until_complete(start_server)
    asyncio.get_event_loop().run_forever()
except KeyboardInterrupt:
    print("End")