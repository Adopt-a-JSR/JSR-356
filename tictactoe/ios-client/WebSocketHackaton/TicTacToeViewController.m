//
//  TicTacToeViewController.m
//  WebSocketHackaton
//
//  Created by Michael Seghers on 6/03/13.
//  Copyright (c) 2013 iDA MediaFoundry. All rights reserved.
//

#import "TicTacToeViewController.h"
#import "SocketRocket/SRWebSocket.h"

#define IAM_P1_WAITING @"p1"
#define P2_JOINED @"p2"
#define IAM_P2 @"p3"

@interface TicTacToeViewController () {
    SRWebSocket *_socket;
    NSString *_symbol;
}

@property (strong, nonatomic) IBOutlet UIButton *field1Button;
@property (strong, nonatomic) IBOutlet UIButton *field2Button;
@property (strong, nonatomic) IBOutlet UIButton *field3Button;
@property (strong, nonatomic) IBOutlet UIButton *field4Button;
@property (strong, nonatomic) IBOutlet UIButton *field5Button;
@property (strong, nonatomic) IBOutlet UIButton *field6Button;
@property (strong, nonatomic) IBOutlet UIButton *field7Button;
@property (strong, nonatomic) IBOutlet UIButton *field8Button;
@property (strong, nonatomic) IBOutlet UIButton *field9Button;
@property (strong, nonatomic) IBOutlet UILabel *statusLabel;

- (IBAction)fieldButtonTouched:(id)sender;

@end

@implementation TicTacToeViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    //NSURL *url = [NSURL URLWithString:@"ws://ec2-54-242-90-129.compute-1.amazonaws.com:80/tictactoeserver/endpoint"];
    NSURL *url = [NSURL URLWithString:@"ws://localhost:8080/tictactoeserver/endpoint"];
    
    _socket = [[SRWebSocket alloc] initWithURL:url];
    _socket.delegate = self;
    [_socket open];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)fieldButtonTouched:(id)sender {
    UIButton *button = sender;
    [button setTitle:_symbol forState:UIControlStateNormal];
    NSString *message = [NSString stringWithFormat:@"%@%d", _symbol, button.tag];
    button.enabled = false;
    [_socket send:message];
}
     
#pragma mark - SocketRocket WebSocket delegate
     
     - (void)webSocket:(SRWebSocket *)webSocket didReceiveMessage:(id)message {
         NSLog(@"Received message via WebSocket: %@", message);
         if ([IAM_P1_WAITING isEqualToString:message]) {
             //I am player one and have to wait for P2 to arive
             self.statusLabel.text = @"Waiting for player 2...";
         } else if ([P2_JOINED isEqualToString:message]) {
             //I am player one and have to wait for P2 to arive
             self.statusLabel.text = @"Player 2 joined! You play O";
             _symbol = @"o";
         } else if ([IAM_P2 isEqualToString:message]) {
             self.statusLabel.text = @"You joined a game! You play X";
             _symbol = @"x";
         } else if ([message hasPrefix:@"o"]) {
             //o is played by other player (well should), put this on board
             int position = [[message substringFromIndex:1] intValue];
             UIButton *button = (UIButton *) [self.view viewWithTag:position];
             [button setTitle:@"o" forState:UIControlStateNormal];
             button.enabled = false;

         } else if ([message hasPrefix:@"x"]) {
             int position = [[message substringFromIndex:1] intValue];
             UIButton *button = (UIButton *) [self.view viewWithTag:position];
             [button setTitle:@"x" forState:UIControlStateNormal];
             button.enabled = false;
         }
     }
     
     - (void)webSocketDidOpen:(SRWebSocket *)webSocket {
         NSLog(@"The WebSocket has been opened!");
         //Unblock user interaction
     }
     
     - (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error {
         NSLog(@"WebSocket failed: %@", error);
     }
     
     - (void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean {
         NSLog(@"WebSocket was closed with code %d and reason %@ (%@)", code, reason, wasClean ? @"CLEAN" : @"UNCLEAN") ;
     }
     

@end
