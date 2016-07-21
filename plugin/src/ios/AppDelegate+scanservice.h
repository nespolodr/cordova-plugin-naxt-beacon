
#import "AppDelegate.h"
#import "AppDelegate+scanservice.h"


@interface AppDelegate ()
- (id) getCommandInstance:(NSString*)className;

@property (nonatomic) ESTBeaconManager *beaconManager;

@end
